package com.example.techsupport.screens.tabs

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.techsupport.Constants
import com.example.techsupport.Utils
import com.example.techsupport.models.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date


@Composable
fun ChatScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var currentMessage by remember { mutableStateOf(TextFieldValue("")) }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(true) }

    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }


    // Fetch chat data from Firebase
    LaunchedEffect(true) {
        fetchChatsFromFirebase(scope) { fetchedChats ->
            chats = fetchedChats
            isLoading.value = false // Set loading to false once data is fetched
        }
    }

    val model = remember {
        GenerativeModel(
            "gemini-2.0-flash-exp",
            Constants.GEMINI_API_KEY, // API key stored securely in BuildConfig
            generationConfig = generationConfig {
                temperature = 1f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 8192
                responseMimeType = "application/json"
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Message list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            reverseLayout = true // Start with the most recent message at the bottom
        ) {
            if (isLoading.value) {
                // Show 5 dummy chat bubbles when loading
                items(5) {
                    ChatBubble(
                        isLoading = true,
                        request = "",
                        response = "",
                        questionOfThought = "",
                        followUpQuestions = emptyList() // Correct initialization for an empty list
                    )
                }
            } else {
                // Show real chat bubbles from the list
                items(chats.size) { index ->
                    val chat = chats[chats.size - 1 - index]

                    // Extract response and question_of_thought from the Map
                    val response = chat.response["response"] ?: "No response available"
                    val questionOfThought =
                        chat.response["question_of_thought"] ?: "No question of thought available"
                    val followUpQuestions =
                        chat.response["follow_up_questions"] as? List<String> ?: emptyList()


                    // Pass extracted values to the ChatBubble
                    ChatBubble(
                        request = chat.request,
                        response = response.toString(),
                        questionOfThought = questionOfThought.toString(),
                        followUpQuestions = followUpQuestions
                    )
                }
            }

        }


        var isLoading by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Upload image button (if needed)
            IconButton(onClick = {
                // Image upload logic placeholder (to be implemented later)
            }) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Upload Image",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Message input field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                BasicTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (currentMessage.text.isNotBlank()) {
                                sendUserMessage(
                                    currentMessage,
                                    model,
                                    scope,
                                    isLoading, chats
                                ) { success ->
                                    // Optionally handle success or failure here
                                }
                            }
                        }
                    )
                )
                if (currentMessage.text.isEmpty()) {
                    Text(
                        text = "Type here...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 16.sp
                    )
                }
            }

            // Send button or loading indicator

            IconButton(onClick = {
                if (currentMessage.text.isNotBlank()) {
                    isLoading = true
                    sendUserMessage(currentMessage, model, scope, isLoading, chats) { success ->
                        isLoading = false
                    }
                }
            }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }
        }
    }
}


private fun sendUserMessage(
    currentMessage: TextFieldValue,
    model: GenerativeModel,
    scope: CoroutineScope,
    isLoading: Boolean,
    chats: List<Chat>,
    onCompletion: (Boolean) -> Unit
) {

    // log chats
    Log.d("chats", chats.takeLast(1).firstOrNull()?.response.toString())

    val userMessage = currentMessage.text
    // Get the current user's ID from Firebase Authentication
    val userId = Utils.getUserId()
    // Get the timestamp when the message is sent (request timestamp)
    val requestTimestamp = Date().time

    // Check if the user is logged in (userId is not null)
    if (userId != null) {
        scope.launch {
            try {
                // Start a chat without chat history
                // Assuming your Chat object has request, response, userId, etc.
                val top3Chats = chats.takeLast(3) // Get the last 3 chats (most recent)

                val chatHistory = top3Chats.map { chat ->
                    content("user") {
                        text("${chat.request}\n")
                    }
                    content("model") {
                        text("${chat.response}\n")
                    }
                }.toList()

                val chat1 = model.startChat(chatHistory) // Pass the chat history to the model


                // Custom Prompt Engineering based on the user message
                val prompt = when {
                    userMessage.contains("repair", ignoreCase = true) -> {
                        "You are a tech support agent that helps users with device repairs. Focus on troubleshooting the problem and guide the user through the necessary repair steps. Ensure your tone is friendly, empathetic, and professional. Start by acknowledging the user's issue, offer clear step-by-step guidance, and ask follow-up questions if needed."
                    }

                    userMessage.contains("error", ignoreCase = true) -> {
                        "You are a tech support agent. Help the user by providing detailed troubleshooting steps for fixing the error they encountered. Ask clarifying questions to gather more details. Maintain a positive, friendly tone throughout, showing empathy for their situation. Offer solutions and ask if the problem is resolved."
                    }

                    userMessage.contains("slow", ignoreCase = true) -> {
                        "You are a tech support agent. Ask the user to provide more details about the performance issue, such as which tasks or apps are causing the device to slow down. Respond with a warm and supportive tone, making the user feel comfortable while troubleshooting the issue."
                    }

                    userMessage.contains("battery", ignoreCase = true) -> {
                        "You are a tech support agent. Help the user troubleshoot battery issues with their device. Ask about battery usage and recent activities. Be empathetic and encourage the user by assuring them that you're here to help. Offer practical tips to improve battery life."
                    }

                    userMessage.contains("wifi", ignoreCase = true) -> {
                        "You are a tech support agent. Guide the user through troubleshooting their Wi-Fi connection issues. Ask them about network settings and potential obstacles. Maintain a friendly and patient tone, and let the user know you're there to help every step of the way."
                    }

                    else -> {
                        "You are a tech support agent. Provide a helpful and logical response based on the user's query. Ask relevant questions to gather more information if needed. Use a friendly, approachable tone to ensure the user feels heard and supported. Let them know that you're here to assist them in resolving the issue."
                    }
                }

                val instructions =
                    "Instructions: Don't respond to question out side the context, just return something like, your question is note relevant to tech, i can help you with something else like ..."
// Final prompt with the user's message
                val finalPrompt =
                    "$prompt \nThe message from the user:\n$userMessage\n $instructions\nPROVIDE YOUR RESPONSE IN A JSON FORMAT FOR EXAMPLE {`question_of_thought`:`The reason why you provided that response`,`response`:`The response`,`follow_up_questions`:[Array of questions]}"

// Send the user's input message along with the final prompt for guidance
                val response = chat1.sendMessage(finalPrompt)

                // Extract AI's response text
                val aiMessage = response.candidates.first().content.parts.first().asTextOrNull()

                // Get the timestamp when the response is received (response timestamp)
                val responseTimestamp = Date().time

                // Calculate the duration in seconds (time taken for the AI to respond)
                val durationInSeconds =
                    (responseTimestamp - requestTimestamp) / 1000 // Convert to seconds


                // Create a Chat object to send to Firebase
                val chatId = Utils.getUniqueId() // Generate a unique chat ID
                // Parse the response using Gson
                val gson = Gson()
                val responseObject: JsonObject = gson.fromJson(aiMessage, JsonObject::class.java)

// Convert JsonObject to a Map<String, String>
                val responseMap: Map<String, String> =
                    gson.fromJson(responseObject, Map::class.java) as Map<String, String>

// Now, save the chat object with the parsed response
                val chat = Chat(
                    id = chatId,
                    userId = userId,
                    request = userMessage,
                    response = responseMap,  // The parsed Map<String, String> as response
                    createdAt = requestTimestamp,
                    duration = durationInSeconds
                )

                // Send the chat data to Firebase Realtime Database
                Utils.getDatabaseReference().getReference("chats")
                    .child(chatId) // Use the unique chat ID
                    .setValue(chat)
                    .addOnSuccessListener {
                        onCompletion(true) // Success callback
                    }
                    .addOnFailureListener {
                        onCompletion(false) // Failure callback
                    }
            } catch (e: Exception) {
                onCompletion(false) // Handle errors gracefully
            }
        }
    }
}


@Composable
fun ChatBubble(
    request: String,
    response: String,
    isLoading: Boolean = false,
    questionOfThought: String,
    followUpQuestions: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // User's request bubble on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (isLoading) {
                    // Placeholder for request when loading

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = request,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // AI's response bubble on the left
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (!isLoading) {
                IconButton(
                    onClick = {
                        // Handle icon click (optional, can be left empty)
                    },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome, // You can use any icon here
                        contentDescription = "User Icon",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (isLoading) {
                    // Placeholder for response when loading

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = response,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                        ) {

                            followUpQuestions.forEach { question ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.tertiary,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                ) {

                                    Text(
                                        text = "$question",
                                        color = MaterialTheme.colorScheme.onTertiary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(16.dp) // Optional: Add padding inside the Card to prevent the text from touching the edges
                                    )

                                }
                            }

                        }

                    }

                }
            }
        }
    }
}

// Function to fetch chats from Firebase
fun fetchChatsFromFirebase(scope: CoroutineScope, onFetched: (List<Chat>) -> Unit) {
    Utils.getDatabaseReference().getReference(Constants.CHATS_DB)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatsList = mutableListOf<Chat>()

                // Loop through all the chat data
                snapshot.children.forEach { chatSnapshot ->
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    if (chat != null) {
                        chatsList.add(chat)
                    }
                }

                // Pass the fetched chats to the Composable
                onFetched(chatsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors
            }
        })
}