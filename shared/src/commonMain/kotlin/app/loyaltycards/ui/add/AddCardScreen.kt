package app.loyaltycards.ui.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.loyaltycards.domain.Store
import app.loyaltycards.ui.components.CircleIconButton
import app.loyaltycards.ui.components.PillButton
import app.loyaltycards.ui.preview.PreviewData
import app.loyaltycards.ui.preview.PreviewTheme
import kotlinx.coroutines.launch

@Composable
fun AddCardScreen(viewModel: AddCardViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AddCardContent(
        state = state,
        onStoreQueryChange = viewModel::onStoreQueryChange,
        onStoreSelected = viewModel::onStoreSelected,
        onCardCodeChange = viewModel::onCardCodeChange,
        onNameOnCardChange = viewModel::onNameOnCardChange,
        onSave = { viewModel.save(onSaved = onBack) },
        onBack = onBack,
    )
}

@Composable
internal fun AddCardContent(
    state: AddCardFormState,
    onStoreQueryChange: (String) -> Unit,
    onStoreSelected: (Store) -> Unit,
    onCardCodeChange: (String) -> Unit,
    onNameOnCardChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            PillButton(
                text = "Add to wallet",
                onClick = onSave,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(56.dp),
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    onClick = onBack,
                )
                Spacer(Modifier.width(14.dp))
                Text("Add card", style = MaterialTheme.typography.displaySmall)
            }

            ScanPanel(
                onClick = {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Scanning is coming in the next update. Enter the code below for now.")
                    }
                },
            )

            OrDivider()

            FieldLabel("Store")
            FormTextField(
                value = state.storeQuery,
                onValueChange = onStoreQueryChange,
                placeholder = "Search, e.g. Lidl Plus",
                error = state.storeError,
                capitalization = KeyboardCapitalization.Words,
            )
            if (state.suggestions.isNotEmpty()) {
                StoreSuggestions(state.suggestions, onSelect = onStoreSelected)
            }

            Spacer(Modifier.height(18.dp))
            FieldLabel("Card code")
            FormTextField(
                value = state.cardCode,
                onValueChange = onCardCodeChange,
                placeholder = "Number under the barcode",
                error = state.codeError,
                keyboardType = KeyboardType.Ascii,
            )

            Spacer(Modifier.height(18.dp))
            FieldLabel("Name on card", optional = true)
            FormTextField(
                value = state.nameOnCard,
                onValueChange = onNameOnCardChange,
                placeholder = "As printed on the card",
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
                onDone = onSave,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Placeholder for the camera scanner, styled as in the design. */
@Composable
private fun ScanPanel(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(176.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF16171A),
        contentColor = Color.White,
    ) {
        Box(Modifier.padding(20.dp)) {
            ViewfinderCorners(Modifier.fillMaxSize())
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BarcodeGlyph()
                Spacer(Modifier.height(14.dp))
                Text("Scan barcode", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Point the camera at the back of your card",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun ViewfinderCorners(modifier: Modifier) {
    Canvas(modifier) {
        val arm = 28.dp.toPx()
        val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        val radius = 10.dp.toPx()
        val w = size.width
        val h = size.height
        // Each corner is an L with a rounded bend, drawn as an arc plus two lines.
        listOf(
            Triple(Offset(0f, 0f), 1f, 1f),
            Triple(Offset(w, 0f), -1f, 1f),
            Triple(Offset(0f, h), 1f, -1f),
            Triple(Offset(w, h), -1f, -1f),
        ).forEach { (corner, dx, dy) ->
            drawLine(Color.White, Offset(corner.x + dx * radius, corner.y), Offset(corner.x + dx * arm, corner.y), stroke.width, StrokeCap.Round)
            drawLine(Color.White, Offset(corner.x, corner.y + dy * radius), Offset(corner.x, corner.y + dy * arm), stroke.width, StrokeCap.Round)
            val startAngle = when {
                dx > 0 && dy > 0 -> 180f
                dx < 0 && dy > 0 -> 270f
                dx > 0 && dy < 0 -> 90f
                else -> 0f
            }
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(
                    if (dx > 0) corner.x else corner.x - 2 * radius,
                    if (dy > 0) corner.y else corner.y - 2 * radius,
                ),
                size = Size(2 * radius, 2 * radius),
                style = stroke,
            )
        }
    }
}

@Composable
private fun BarcodeGlyph() {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        listOf(4, 3, 4, 3, 4, 3).forEach { width ->
            Box(Modifier.size(width = width.dp, height = 26.dp).clip(RoundedCornerShape(1.dp)).background(Color.White))
        }
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        Text(
            text = "or enter manually",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun FieldLabel(text: String, optional: Boolean = false) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(text) }
            if (optional) {
                withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append(" (optional)") }
            }
        },
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge) },
        textStyle = MaterialTheme.typography.bodyLarge,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            errorContainerColor = colors.surface,
            focusedBorderColor = colors.onSurface,
            unfocusedBorderColor = colors.outline,
            unfocusedPlaceholderColor = colors.onSurfaceVariant,
            focusedPlaceholderColor = colors.onSurfaceVariant,
        ),
    )
}

@Composable
private fun StoreSuggestions(stores: List<Store>, onSelect: (Store) -> Unit) {
    // Keep the suggestions visible above the keyboard while typing.
    val bringIntoView = remember { BringIntoViewRequester() }
    LaunchedEffect(stores) { bringIntoView.bringIntoView() }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).bringIntoViewRequester(bringIntoView),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column {
            stores.forEachIndexed { index, store ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Surface(onClick = { onSelect(store) }, color = Color.Transparent) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(width = 28.dp, height = 20.dp).clip(RoundedCornerShape(5.dp)).background(Color(store.colorArgb)))
                        Spacer(Modifier.width(14.dp))
                        Text(store.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Preview(name = "Add card", widthDp = 390, heightDp = 844)
@Composable
private fun AddCardPreview() = PreviewTheme {
    AddCardContent(AddCardFormState(), {}, {}, {}, {}, onSave = {}, onBack = {})
}

@Preview(name = "Add card, store suggestions", widthDp = 390, heightDp = 844)
@Composable
private fun AddCardSuggestionsPreview() = PreviewTheme {
    AddCardContent(AddCardFormState(storeQuery = "Li"), {}, {}, {}, {}, onSave = {}, onBack = {})
}

@Preview(name = "Add card, missing fields", widthDp = 390, heightDp = 844)
@Composable
private fun AddCardErrorsPreview() = PreviewTheme {
    AddCardContent(AddCardFormState(showErrors = true), {}, {}, {}, {}, onSave = {}, onBack = {})
}
