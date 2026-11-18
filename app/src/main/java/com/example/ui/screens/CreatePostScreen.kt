package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.GoldFilter
import com.example.ui.components.GoldMediaImage
import com.example.ui.theme.LocalGoldTokens

private val StudioSamples = listOf(
    "drawable:img_gold_architecture" to "Courtyard",
    "drawable:img_gold_timepiece" to "Timepiece",
    "drawable:img_gold_dunes" to "Dunes",
    "drawable:img_gold_espresso" to "Espresso"
)

private val PostCategories = listOf("Personal", "Architecture", "Horology", "Travel", "Lifestyle")

@Composable
fun CreatePostScreen(
    onPublish: (
        pickedUri: Uri?,
        fallbackDrawableKey: String,
        caption: String,
        location: String,
        category: String,
        filter: GoldFilter,
        isGoldExclusive: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGoldTokens.current
    val context = LocalContext.current

    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var fallbackSampleKey by rememberSaveable { mutableStateOf("drawable:img_gold_timepiece") }
    var selectedFilterName by rememberSaveable { mutableStateOf(GoldFilter.GOLD_24K.name) }
    var caption by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Personal") }
    var isGoldExclusive by rememberSaveable { mutableStateOf(true) }

    val selectedFilter = remember(selectedFilterName) { GoldFilter.fromName(selectedFilterName) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Create Gold Post",
                        style = MaterialTheme.typography.headlineMedium,
                        color = tokens.highlight
                    )
                    Text(
                        text = "Grade your shot with 24K darkroom presets",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.mutedText
                    )
                }
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("pick_photo_button")
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.action_pick_photo))
                }
            }

            // Live Filtered Media Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, tokens.accent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .background(tokens.surfaceCard)
            ) {
                if (pickedUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(pickedUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Selected photo preview",
                        contentScale = ContentScale.Crop,
                        colorFilter = selectedFilter.toColorFilter(),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    GoldMediaImage(
                        mediaKey = fallbackSampleKey,
                        contentDescription = "Studio sample preview",
                        filter = selectedFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.72f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = selectedFilter.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.highlight
                    )
                }
            }

            // Studio Sample Switcher (when user hasn't picked from device gallery or wants quick studio art)
            Text(
                text = if (pickedUri == null) "Studio Canvas (or tap Choose Photo)" else "Switch to Studio Canvas",
                style = MaterialTheme.typography.labelLarge,
                color = tokens.mutedText
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(StudioSamples) { (key, label) ->
                    val isSelected = pickedUri == null && fallbackSampleKey == key
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                pickedUri = null
                                fallbackSampleKey = key
                            }
                            .testTag("sample_canvas_$label")
                    ) {
                        GoldMediaImage(
                            mediaKey = key,
                            contentDescription = label,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) tokens.accent else tokens.hairline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) tokens.highlight else tokens.mutedText
                        )
                    }
                }
            }

            // Gold Presets
            Text(
                text = "24K Filter Preset",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(GoldFilter.entries) { filter ->
                    val active = selectedFilter == filter
                    FilterChip(
                        selected = active,
                        onClick = { selectedFilterName = filter.name },
                        label = { Text(filter.displayName) },
                        leadingIcon = if (active) {
                            { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tokens.container,
                            selectedLabelColor = tokens.highlight,
                            selectedLeadingIconColor = tokens.accent
                        ),
                        modifier = Modifier.testTag("filter_chip_${filter.name}")
                    )
                }
            }

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption") },
                placeholder = { Text("Write a refined story behind this frame...") },
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_caption_input")
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("e.g., Udaipur, Milan, Geneva") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_location_input")
            )

            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PostCategories) { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tokens.container,
                            selectedLabelColor = tokens.highlight
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(tokens.surfaceCard)
                    .border(1.dp, tokens.hairline, RoundedCornerShape(16.dp))
                    .clickable { isGoldExclusive = !isGoldExclusive }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mark as 24K Exclusive",
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.highlight
                    )
                    Text(
                        text = "Adds a metallic bullion badge and gold border in feed",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.mutedText
                    )
                }
                Switch(
                    checked = isGoldExclusive,
                    onCheckedChange = { isGoldExclusive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = tokens.accent
                    )
                )
            }

            Button(
                onClick = {
                    val finalCaption = caption.trim().ifEmpty {
                        "Captured in ${selectedFilter.displayName} via InstaGold Studio."
                    }
                    onPublish(
                        pickedUri,
                        fallbackSampleKey,
                        finalCaption,
                        location,
                        category,
                        selectedFilter,
                        isGoldExclusive
                    )
                    caption = ""
                    location = ""
                    pickedUri = null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_post_button")
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.action_publish),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
