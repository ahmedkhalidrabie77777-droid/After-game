package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InteractiveStarRatingBar
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldStar
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onAdd: (
        title: String,
        titleAr: String,
        developer: String,
        releaseYear: Int,
        genres: String,
        platforms: String,
        synopsis: String,
        userRating: Float,
        status: String,
        hoursPlayed: Float,
        userReview: String,
        platformPlayed: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf("") }
    var titleAr by remember { mutableStateOf("") }
    var developer by remember { mutableStateOf("") }
    var releaseYearText by remember { mutableStateOf("2024") }
    var genres by remember { mutableStateOf("Action RPG") }
    var platforms by remember { mutableStateOf("PC, PS5, Xbox") }
    var synopsis by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(4.0f) }
    var status by remember { mutableStateOf("COMPLETED") }
    var hoursText by remember { mutableStateOf("25") }
    var reviewText by remember { mutableStateOf("") }
    var platformPlayed by remember { mutableStateOf("PlayStation 5") }

    val statusOptions = listOf(
        "PLAYING" to "ألعبها الآن",
        "COMPLETED" to "مكتملة",
        "BACKLOG" to "في الانتظار",
        "MASTERED" to "100% بلاتينيوم"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "إضافة لعبة جديدة للسجل",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "سجل أي لعبة لم تجدها في القائمة مع تقييمك ورأيك",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("اسم اللعبة بالإنجليزية (مثال: Silent Hill 2)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_title_input"),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = titleAr,
                onValueChange = { titleAr = it },
                label = { Text("اسم اللعبة بالعربية (اختياري)") },
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = developer,
                    onValueChange = { developer = it },
                    label = { Text("المطور / الناشر") },
                    modifier = Modifier.weight(1f),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = releaseYearText,
                    onValueChange = { releaseYearText = it },
                    label = { Text("سنة الإصدار") },
                    modifier = Modifier.weight(1f),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = genres,
                onValueChange = { genres = it },
                label = { Text("التصنيف (مثال: Action, Survival Horror)") },
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating
            Text(text = "تقييمك المبدئي للعبة", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            InteractiveStarRatingBar(rating = rating, onRatingChanged = { rating = it })

            Spacer(modifier = Modifier.height(12.dp))

            // Status chips
            Text(text = "حالة اللعب", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusOptions.forEach { (key, label) ->
                    val isSel = status == key
                    FilterChip(
                        selected = isSel,
                        onClick = { status = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = hoursText,
                    onValueChange = { hoursText = it },
                    label = { Text("ساعات اللعب") },
                    modifier = Modifier.weight(1f),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = platformPlayed,
                    onValueChange = { platformPlayed = it },
                    label = { Text("الجهاز (مثال: PS5)") },
                    modifier = Modifier.weight(1f),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("ملاحظاتك ومراجعتك للعبة...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val year = releaseYearText.toIntOrNull() ?: 2024
                        val hrs = hoursText.toFloatOrNull() ?: 0f
                        onAdd(
                            title,
                            titleAr,
                            developer,
                            year,
                            genres,
                            platforms,
                            synopsis,
                            rating,
                            status,
                            hrs,
                            reviewText,
                            platformPlayed
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_add_game_button")
            ) {
                Text(text = "إضافة اللعبة إلى السجل", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("إلغاء", color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkSurfaceVariant,
    unfocusedContainerColor = DarkSurfaceVariant,
    focusedBorderColor = CyberPrimary,
    unfocusedBorderColor = BorderSubtle,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)
