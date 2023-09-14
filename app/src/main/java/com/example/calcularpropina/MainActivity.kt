package com.example.calcularpropina

import android.graphics.drawable.Icon
import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.painterResource
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
    //variables de estado
    var roundUp by remember { mutableStateOf(false) }
    var entradaPropina by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf(" ")}

    // trasformamos lo que recibiomos en los textField a doble y lo pasamos a calcular propina
    val porcentajePropina=entradaPropina.toDoubleOrNull() ?:0.0
    val amount=amountInput.toDoubleOrNull() ?: 0.0
    val propina= calculateTip(amount ,porcentajePropina,roundUp)//calcula la propina y redondea segun el estado de rounduup

    Column(
        modifier = Modifier
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
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
            icono=R.drawable.money,
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
            icono = R.drawable.percent,
            opcionesTeclado = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),//cambia la imagen de accion de teclado
            value = entradaPropina,
            valorCambia = { entradaPropina=it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth())
        RedondearPropina(
            roundUp = roundUp,
            onRoundUpChanged = {roundUp = it} ,
            modificador = Modifier.padding(bottom = 32.dp))
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
    @DrawableRes icono: Int,//parametro para agregar un icono
    opcionesTeclado: KeyboardOptions,//para modificar opciones de teclado
    value: String,//valor que se Muestra
    valorCambia: (String)-> Unit, //un funcion tipo string
    modifier: Modifier = Modifier) //modificadores


{

    //value es un cuadro de texto que muestra el valor de cadena que pasas aquí.
    // El parámetro onValueChange es la devolución de llamada lambda que se activa cuando el usuario ingresa texto en el cuadro.
    TextField(
        value=value,
        leadingIcon ={ Icon(painter= painterResource(id = icono),null) },
        onValueChange = valorCambia ,
        label = { Text(stringResource(label) ) },
        singleLine = true,//condensa el cuadro de texto en una sola linea
        //fija el tipo de teclado en numeros a din de ingresar numeros
        keyboardOptions = opcionesTeclado,//pasamo la opciones de teclado segun los parametros que entren
        modifier= modifier
    )
}

@Composable//roundTheTipRow
fun RedondearPropina(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean)-> Unit ,
    modificador: Modifier= Modifier){

    //una fila
    Row(
        modifier= modificador
            .fillMaxWidth()//ancho al maximo de la pantalla
            .size(48.dp), //tamanio
        verticalAlignment=Alignment.CenterVertically //centra la alineacion
    ){
        Text(text = stringResource(R.string.round_up_tip))//muestra el texto de la fila
        Switch(
            modifier= modificador
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),//alinea el elemento al final de la pantalla
            checked = roundUp, //indica si el interruptor esta  marcado
            onCheckedChange = onRoundUpChanged )//devolucion de la llamado cuado se haga click
    }

}


private fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {

    var tip = tipPercent / 100 * amount

    //verificamos si es true se redondea la propina
    if(roundUp){
        tip=kotlin.math.ceil(tip)
    }

    return NumberFormat.getCurrencyInstance().format(tip)
}



