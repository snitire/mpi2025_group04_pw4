package com.groupfour.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupfour.calculator.ui.theme.CalculatorTheme

class CalculatorViewModel : ViewModel() {
    var previousVal : String = "9"
    var currentVal : String = "99999999999999999999"
    var currentMode : String = "+"
    var memoryVal : String = ""

    // TODO handle numbers that get really long
    fun handleBtnPress(label: String) {

    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Calculator()
            }
        }
    }
}

@Composable
@Preview
fun Calculator(
    viewModel: CalculatorViewModel = viewModel()
) {
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // display = 1/3, buttons = 2/3 of the screen
            ValueDisplay(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            )
            ButtonPad(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun ValueDisplay(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier
) {
    Box (
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Column {
            val commonMod = Modifier.fillMaxWidth()
            DisplayText(
                viewModel.previousVal,
                modifier = commonMod.weight(1f)
            )
            DisplayText(
                viewModel.currentMode,
                modifier = commonMod.weight(0.5f)
            )
            DisplayText(
                viewModel.currentVal,
                modifier = commonMod.weight(1f)
            )
        }
    }
}

@Composable
fun DisplayText(
    text: String,
    modifier: Modifier
) {
    Box (
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        BasicText (
            text = text,
            maxLines = 2,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 16.sp,
                maxFontSize = 72.sp
            )
        )
    }
}

@Composable
fun ButtonPad(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val buttonCount = listOf(
            "MS", "MR", "MC", "/",
            "7", "8", "9", "x",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "C", "0", ".", "="
        )

        val cols = 4
        val rows = 5
        val pad = 4.dp
        val btnHeight = (maxHeight - pad*rows - pad*2) / rows

        LazyVerticalGrid(
            columns = GridCells.Fixed(cols),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(pad),
            verticalArrangement = Arrangement.spacedBy(pad),
            horizontalArrangement = Arrangement.spacedBy(pad)
        ) {
            items(buttonCount) { label ->
                CalcBtn(
                    label,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(btnHeight)
                )
            }
        }
    }
}

@Composable
fun CalcBtn(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Button (
        onClick = onClick,
        modifier = modifier
    ) {
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val size = (maxHeight.value * 0.45).sp
            Text(
                text = label,
                fontSize = size,
                softWrap = false
            )
        }
    }
}