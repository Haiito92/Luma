package com.example.luma.ui.guardians

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.app.LumaAppPreviewScaffold
import com.example.luma.ui.theme.LumaTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FollowSosScreen(
    walkId: String,
    viewModel: FollowSosViewModel = viewModel(factory = FollowSosViewModelFactory)
) {
    val selectedPerson by viewModel.selectedPerson.collectAsStateWithLifecycle()

    FollowSosContent(person = selectedPerson)
}

@Composable
fun FollowSosContent(person: FollowedPerson?) {
    val remainingMinutes = person?.remainingMinutes ?: 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(96.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$remainingMinutes minutes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CourseTrajectoryPlaceholder(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun CourseTrajectoryPlaceholder(modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val coneColor = MaterialTheme.colorScheme.primaryContainer
    val arrowColor = MaterialTheme.colorScheme.error

    Canvas(modifier = modifier) {
        val center = Offset(size.width * 0.5f, size.height * 0.5f)
        val target = Offset(size.width * 0.68f, size.height * 0.25f)

        // Ligne de trajectoire (flèche vers la destination)
        drawLine(
            color = arrowColor,
            start = center,
            end = target,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        val angle = atan2(target.y - center.y, target.x - center.x)
        val arrowHeadLength = 18.dp.toPx()
        val arrowHeadAngle = Math.toRadians(28.0).toFloat()

        val leftWing = Offset(
            x = target.x - arrowHeadLength * cos(angle - arrowHeadAngle),
            y = target.y - arrowHeadLength * sin(angle - arrowHeadAngle)
        )
        val rightWing = Offset(
            x = target.x - arrowHeadLength * cos(angle + arrowHeadAngle),
            y = target.y - arrowHeadLength * sin(angle + arrowHeadAngle)
        )

        drawLine(
            color = arrowColor,
            start = target,
            end = leftWing,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = arrowColor,
            start = target,
            end = rightWing,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Cône d'orientation au-dessus du point de position
        val coneHeight = 24.dp.toPx()
        val coneHalfWidth = 12.dp.toPx()
        val conePath = Path().apply {
            moveTo(center.x, center.y - 6.dp.toPx())
            lineTo(center.x - coneHalfWidth, center.y - coneHeight)
            lineTo(center.x + coneHalfWidth, center.y - coneHeight)
            close()
        }
        drawPath(path = conePath, color = coneColor)

        // Point de position actuel
        drawCircle(
            color = primaryColor,
            radius = 8.dp.toPx(),
            center = center
        )
    }
}

@Preview
@Composable
fun FollowingCourseScreenPreview() {
    val samplePerson = FollowedPerson(
        id = "1",
        name = "Jean-Baptiste Michelin",
        remainingMinutes = 5
    )

    LumaTheme {
        LumaAppPreviewScaffold {
            FollowSosContent(person = samplePerson)
        }
    }
}
