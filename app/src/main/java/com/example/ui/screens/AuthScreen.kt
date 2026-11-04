package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.BrandPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    repository: Teach2LearnRepository,
    initialIsSignUp: Boolean = false,
    onAuthSuccess: () -> Unit,
    onBackToLanding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(initialIsSignUp) }

    // Sign In Fields (Clean and empty by default for new users)
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Sign Up Fields
    var fullName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var educationLevel by remember { mutableStateOf("B.Tech") }
    var college by remember { mutableStateOf("Government College of Engineering") }
    var department by remember { mutableStateOf("CSE") }
    var yearSemester by remember { mutableStateOf("1st Year / 1st Sem") }

    val allSubjects = listOf(
        "C Programming", "Python", "Data Structures", "Java",
        "Mathematics", "Engineering Mechanics", "Electrical Engineering",
        "AI & Data Science", "Operating Systems", "DBMS"
    )

    var selectedLearnSubjects by remember {
        mutableStateOf(setOf("C Programming", "Python"))
    }
    var selectedTeachSubjects by remember {
        mutableStateOf(emptySet<String>())
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToLanding) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "TEACH2LEARN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isSignUp) "Create Your Account" else "Welcome Back 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (isSignUp) "Join the peer-to-peer education movement" else "Sign in to continue learning and teaching",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Mode Toggle
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isSignUp = false },
                        color = if (!isSignUp) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Text(
                            text = "Sign In",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = if (!isSignUp) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isSignUp = true },
                        color = if (isSignUp) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Text(
                            text = "Sign Up",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = if (isSignUp) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            errorMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        if (!isSignUp) {
            // SIGN IN TAB
            item {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("Enter your email address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signin_email_field"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signin_password_field"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Text(
                            text = "Remember me",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    TextButton(onClick = {
                        errorMessage = "Password reset instructions sent to $emailInput"
                    }) {
                        Text(
                            text = "Forgot password?",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (emailInput.isBlank() || passwordInput.isBlank()) {
                            errorMessage = "Please enter valid credentials"
                        } else {
                            repository.loginUser(emailInput, passwordInput)
                            onAuthSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("signin_submit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Sign In to Teach2Learn", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Demo Profiles
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "🚀 One-Click Demo Profiles:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val demoUsers = listOf(
                        Triple("user_alok", "Student Learner", "Learner / Expert in C & Python"),
                        Triple("user_rahul", "Peer Mentor", "Master Mentor (C, DSA, 86 helped)"),
                        Triple("user_sneha", "Campus Educator", "Grand Mentor (Python, AI/DS, 112 helped)")
                    )

                    demoUsers.forEach { (uid, name, role) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable {
                                    repository.switchUser(uid)
                                    onAuthSuccess()
                                },
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = role, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(text = "Log In →", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val context = LocalContext.current
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Teach2LearnRepository.OFFICIAL_WEBSITE_URL))
                                context.startActivity(intent)
                            }
                            .testTag("auth_open_web_portal"),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("🌐", fontSize = 14.sp)
                                Text(
                                    text = "Web Portal: teach2learn-app.netlify.app",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Icon(
                                Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // SIGN UP TAB
            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = signUpEmail,
                    onValueChange = { signUpEmail = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = signUpPassword,
                    onValueChange = { signUpPassword = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = educationLevel,
                        onValueChange = { educationLevel = it },
                        label = { Text("Level") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Dept") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / University") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = yearSemester,
                    onValueChange = { yearSemester = it },
                    label = { Text("Year / Semester") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // WHAT DO YOU WANT TO LEARN?
                Text(
                    text = "What do you want to learn? (Select multiple)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allSubjects.forEach { subj ->
                        val isSelected = selectedLearnSubjects.contains(subj)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedLearnSubjects = if (isSelected) {
                                    selectedLearnSubjects - subj
                                } else {
                                    selectedLearnSubjects + subj
                                }
                            },
                            label = { Text(subj, fontSize = 12.sp) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // WHAT CAN YOU TEACH?
                Text(
                    text = "What can you teach? (Turn your strength into peer power)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allSubjects.forEach { subj ->
                        val isSelected = selectedTeachSubjects.contains(subj)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTeachSubjects = if (isSelected) {
                                    selectedTeachSubjects - subj
                                } else {
                                    selectedTeachSubjects + subj
                                }
                            },
                            label = { Text(subj, fontSize = 12.sp) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (fullName.isBlank() || signUpEmail.isBlank() || signUpPassword.isBlank()) {
                            errorMessage = "Please fill in all mandatory fields"
                        } else if (signUpPassword != confirmPassword) {
                            errorMessage = "Passwords do not match"
                        } else {
                            repository.registerUser(
                                fullName = fullName,
                                email = signUpEmail,
                                educationLevel = educationLevel,
                                college = college,
                                department = department,
                                yearSemester = yearSemester,
                                learnList = selectedLearnSubjects.toList(),
                                teachList = selectedTeachSubjects.toList()
                            )
                            onAuthSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("signup_submit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Complete Registration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
