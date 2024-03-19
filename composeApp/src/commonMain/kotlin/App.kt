import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import greetingskmp.composeapp.generated.resources.Res
import greetingskmp.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        AppContent()
        //var showContent by remember { mutableStateOf(false) }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
    }
}

@Composable
fun AppContent() {
    var showMatrix by remember { mutableStateOf(false) }
    val golfers = remember { mutableStateListOf<Golfer>() }

    var holes = remember {
        mutableStateListOf<Hole>(
            Hole(1, 6),
            Hole(2, 8),
            Hole(3, 18),
            Hole(4, 10),
            Hole(5, 14),
            Hole(6, 12),
            Hole(7, 4),
            Hole(8, 16),
            Hole(9, 2),
            Hole(10, 11),
            Hole(11, 7),
            Hole(12, 17),
            Hole(13, 1),
            Hole(14, 13),
            Hole(15, 9),
            Hole(16, 5),
            Hole(17, 15),
            Hole(18, 3)
        )
    }
    if (!showMatrix) {
        GolferInputScreen(golfers, holes) {
            showMatrix = true
        }
    } else {
        BumpMatrixScreen(golfers, holes) {
            showMatrix = false
        }
    }
}

@Composable
fun GolferInputScreen(golfers: MutableList<Golfer>, holes: MutableList<Hole>, onCalculateBumps: () -> Unit) {
    var golferName by remember { mutableStateOf("") }
    var golferBumps by remember { mutableStateOf("") }
    var holeDifficultiesInput by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Apply padding to the entire Column
    ) {
        Spacer(Modifier.height(16.dp)) // Add a top spacer for better spacing

        TextField(
            value = golferName,
            onValueChange = { golferName = it },
            label = { Text("Golfer Name") },
            modifier = Modifier.fillMaxWidth() // Make TextField fill the width
        )

        TextField(
            value = golferBumps,
            onValueChange = { golferBumps = it },
            label = { Text("Bumps") },
            modifier = Modifier.fillMaxWidth() // Make TextField fill the width
        )

        Button(
            onClick = {
                golfers.add(Golfer(golferName, golferBumps.toInt()))
                golferName = ""
                golferBumps = ""
            },
            modifier = Modifier.fillMaxWidth() // Make Button fill the width
        ) {
            Text("Add Golfer")
        }

        // Show a list of golfers and bumps
        LazyColumn {
            items(golfers) { golfer ->
                Text("${golfer.name} (${golfer.bumps} bumps)")
            }
        }

        Button(
            onClick = onCalculateBumps,
            modifier = Modifier.fillMaxWidth() // Make Button fill the width
        ) {
            Text("Calculate Bumps")
        }

        Spacer(Modifier.height(32.dp)) // Add spacing between elements

        // Section for hole difficulties input
        var holeDifficultiesInput by remember { mutableStateOf(holes.joinToString(", ") { it.difficulty.toString() }) }

        TextField(
            value = holeDifficultiesInput,
            onValueChange = { holeDifficultiesInput = it },
            label = { Text("Current Hole Difficulties (e.g., '6, 8, 18...')") },
            modifier = Modifier.fillMaxWidth() // Make TextField fill the width
        )
        Button(
            onClick = {
                val difficulties = holeDifficultiesInput.split(",").mapNotNull { it.trim().toIntOrNull() }
                updateHoleDifficulties(holes, difficulties)
            },
            modifier = Modifier.fillMaxWidth() // Make Button fill the width
        ) {
            Text("Update Hole Difficulty")
        }

//        // Show a list of holes and their difficulties
//        LazyColumn {
//            items(holes) { hole ->
//                Text("Hole ${hole.number}: ${hole.difficulty}")
//            }
//        }
    }
}

fun calculateBumps(golfers: List<Golfer>, holes: List<Hole>): Map<String, List<Int>> {
    // Sort the holes by difficulty, descending
    val sortedHoles = holes.sortedBy { it.difficulty }

    // Create a map to hold the result
    val golferBumps = mutableMapOf<String, List<Int>>()

    // Assign bumps for each golfer
    golfers.forEach { golfer ->
        var thisBump = golfer.bumps
        if (golfer.bumps > 18) {
            thisBump = thisBump-18
        }

        val bumps = sortedHoles.take(thisBump).map { it.number }
        golferBumps[golfer.name] = bumps
    }

    return golferBumps
}

@Composable
fun BumpMatrixScreen(golfers: List<Golfer>, holes: List<Hole>, onBack: () -> Unit) {
    // Handle the system back button press
    //xxx BackHandler(onBack = onBack)
    // Calculate the bumps matrix
    val bumpMatrix = calculateBumps(golfers, holes)
    //Log.d("tag", "$bumpMatrix")

    // Display the matrix
    //add column headers for the name of each golfer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }

        // Display a row with the first cell labeled 'hole' and another cell for the name of each golfer
        // This row will be made more prominent
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray) // Example of making the first row more prominent
                .padding(8.dp)
        ) {
            Text(
                "Hole",
                modifier = Modifier.weight(1f),
//                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp) // Increase font size to make it more prominent
            )
            golfers.forEach { golfer ->
                Text(
                    text = "${golfer.name}:${golfer.bumps}",
                    //style = MaterialTheme.typography.bodyMedium, // Original style commented out, adjust as needed
                    modifier = Modifier.weight(1f),
//                    fontSize = 16.sp // Slightly larger text for emphasis
                )

            }
        }

        LazyColumn {
            items(holes.sortedBy { it.number }) { hole ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "${hole.number} (${hole.difficulty}): ",
                        //style = MaterialTheme.typography.body1, // Style can be adjusted as needed
                        modifier = Modifier.weight(1f)
                    )
                    // for each golfer in golfers write a 1 if they get a bump on this hole, 0 if not
                    golfers.forEach { golfer ->
                        val getsBump = bumpMatrix[golfer.name]?.contains(hole.number) ?: false
                        var bumpText = if (getsBump) "YES" else "-"
                        if (golfer.bumps > 18) {
                            bumpText = if (getsBump) "YESx2" else "YES"
                        }
                        Text(
                            text = bumpText,
                            //style = MaterialTheme.typography.body1, // Style can be adjusted as needed
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }


}

fun updateHoleDifficulties(holes: MutableList<Hole>, difficulties: List<Int>) {
    difficulties.forEachIndexed { index, difficulty ->
        if(index < holes.size) {
            holes[index] = holes[index].copy(difficulty = difficulty)
        }
    }
}
