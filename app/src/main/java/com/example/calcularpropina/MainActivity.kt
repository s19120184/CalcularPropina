package com.example.calcularpropina

import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calcularpropina.ui.theme.CalcularPropinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalcularPropinaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTimeLayout() {

    var entradaPropina by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf(" ")}

    // trasformamos lo que recibiomos en los textField a doble y lo pasamos a calcular propina
    val porcentajePropina=entradaPropina.toDoubleOrNull() ?:0.0
    val amount=amountInput.toDoubleOrNull() ?: 0.0
    val propina= calculateTip(amount ,porcentajePropina)//calcula la propina

    Column(
        modifier = Modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(alignment = Alignment.Start)
        )
        EditarCampoNumero(
            label=R.string.bill_amount,
            opcionesTeclado = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next),//cambia la imagen de accion de teclado
            value = amountInput,
            valorCambia = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth())
        EditarCampoNumero(//esto agrega otro cuadro de texto
            label=R.string.how_was_the_service,
            opcionesTeclado = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),//cambia la imagen de accion de teclado
            value = entradaPropina,
            valorCambia = { entradaPropina=it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth())
        Text(
            text = stringResource(R.string.tip_amount, propina),
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
     CalcularPropinaTheme {
        TipTimeLayout()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable  //editNumberField
fun EditarCampoNumero(
    @StringRes label:Int, //id del recurso
    opcionesTeclado: KeyboardOptions,//para modificar opciones de teclado
    value: String,//valor que se Muestra
    valorCambia: (String)-> Unit, //un funcion tipo string
    modifier: Modifier = Modifier) //modificadores


{

    //value es un cuadro de texto que muestra el valor de cadena que pasas aquí.
    // El parámetro onValueChange es la devolución de llamada lambda que se activa cuando el usuario ingresa texto en el cuadro.
    TextField(
        value=value,
        onValueChange = valorCambia ,
        label = { Text(stringResource(label) ) },
        singleLine = true,//condensa el cuadro de texto en una sola linea
        //fija el tipo de teclado en numeros a din de ingresar numeros
        keyboardOptions = opcionesTeclado,//pasamo la opciones de teclado segun los parametros que entren
        modifier= modifier
    )
}


private fun calculateTip(amount: Double, tipPercent: Double = 15.0): String {
    val tip = tipPercent / 100 * amount
    return NumberFormat.getCurrencyInstance().format(tip)
}



