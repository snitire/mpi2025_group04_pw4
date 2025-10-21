package com.groupfour.calculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupfour.calculator.ui.theme.CalculatorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    val MAX_DIGITS = 20 // because my phone limits it to 20 so :shrug:

    private val _previousVal = MutableStateFlow("")
    val previousVal: StateFlow<String> = _previousVal.asStateFlow()
    private val _currentVal = MutableStateFlow("0")
    val currentVal: StateFlow<String> = _currentVal.asStateFlow()
    private val _currentMode = MutableStateFlow("")
    val currentMode: StateFlow<String> = _currentMode.asStateFlow()

    private var memoryVal : String = "" // not displayed on the ui

    // TODO handle numbers that get really long
    fun handleBtnPress(label: String) {
        //_currentVal.value += label
        when (label) {
            "C" -> clear()
            "0","1","2","3","4","5","6","7","8","9" -> digit(label)
            "+","-","x","/" -> mode(label)
            "." -> decimal()
            "MS" -> memSave()
            "MC" -> memClear()
            "MR" -> memRead()
            "=" -> calculate()
            else -> {} // wtf
        }
    }

    fun inputIsEmpty(): Boolean {
        return _currentVal.value.isEmpty() ||
                _currentVal.value == "-" ||
                _currentVal.value.toDouble() == 0.0
    }

    fun clear() {
        if (inputIsEmpty()) {
            // full clear
            _currentVal.value = "0"
            _currentMode.value = ""
            _previousVal.value = ""
        } else {
            // clear just the current input
            _currentVal.value = "0"
        }
    }

    fun digit(n: String) {
        // replace empty input or just 0 with a number
        // also handle -0
        if (inputIsEmpty() && !_currentVal.value.contains(".")) {
            if (_currentVal.value.startsWith("-")) {
                _currentVal.value = "-$n"
            } else {
                _currentVal.value = n
            }
        } else {
            if (_currentVal.value.length < MAX_DIGITS) _currentVal.value += n
        }
    }

    fun mode(m: String) {
        // handle negating
        if (m == "-" && inputIsEmpty()) {
            _currentVal.value = m
            return
        }

        // push current input to the top if its empty
        if (_previousVal.value.isEmpty()) {
            _previousVal.value = _currentVal.value
            _currentVal.value = "0"
        }

        _currentMode.value = m
    }

    fun decimal() {
        if (inputIsEmpty()) {
            if (_currentVal.value.startsWith("-")) {
                _currentVal.value = "-0."
            } else {
                _currentVal.value = "0."
            }
        }
        // im sure this wont mess with locale specific calculations haha
        else if (!_currentVal.value.contains(".")) {
            _currentVal.value += "."
        }
    }

    fun memRead() {
        if (!memoryVal.isEmpty()) _currentVal.value = memoryVal
    }

    // return bool based on whether something was actually saved
    fun memSave(): Boolean {
        if (inputIsEmpty() && !_previousVal.value.isEmpty()) {
            memoryVal = _previousVal.value
            return true
        } else if (inputIsEmpty()) {
            return false
        } else {
            memoryVal = _currentVal.value
            return true
        }
    }

    // return bool based on whether something was actually cleared
    fun memClear(): Boolean {
        if (memoryVal.isEmpty()) return false
        memoryVal = ""
        return true
    }

    fun calculate() {
        var result : Double = 0.0

        when (_currentMode.value) {
            "" -> {
                result = _currentVal.value.toDouble()
            }
            "+" -> {
                result = _previousVal.value.toDouble() + _currentVal.value.toDouble()
            }
            "-" -> {
                result = _previousVal.value.toDouble() - _currentVal.value.toDouble()
            }
            "x" -> {
                result = _previousVal.value.toDouble() * _currentVal.value.toDouble()
            }
            "/" -> {
                if (!inputIsEmpty()) {
                    result = _previousVal.value.toDouble() / _currentVal.value.toDouble()
                } else {
                    return
                }
            }
        }

        if (result.toString().length > MAX_DIGITS) {
            // scientific notation
            _previousVal.value = "%.${MAX_DIGITS - 6}e".format(result)
        } else {
            _previousVal.value = result.toString()
        }
        _currentMode.value = ""
        _currentVal.value = "0"
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
fun Calculator() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // display = 1/2, buttons = 1/2 of the screen
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

            val previousVal by viewModel.previousVal.collectAsState()
            val currentMode by viewModel.currentMode.collectAsState()
            val currentVal by viewModel.currentVal.collectAsState()

            DisplayText(
                previousVal,
                modifier = commonMod.weight(1f)
            )
            DisplayText(
                currentMode,
                modifier = commonMod.weight(0.5f)
            )
            DisplayText(
                currentVal,
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
    val textColor = LocalContentColor.current
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
            ),
            style = TextStyle(
                color = textColor
            )
        )
    }
}

@Composable
fun ButtonPad(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier
) {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = modifier
    ) {
        val buttons = listOf(
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
            items(buttons) { label ->
                CalcBtn(
                    label,
                    onClick = {
                        when (label) {
                            "MS" -> {
                                // UX yeahh
                                if (viewModel.memSave()) {
                                    Toast.makeText(context, "Saved current value to memory", Toast.LENGTH_SHORT).show()
                                }
                            }
                            "MC" -> {
                                if (viewModel.memClear()) {
                                    Toast.makeText(context, "Cleared value from memory", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else -> viewModel.handleBtnPress(label)
                        }
                    },
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