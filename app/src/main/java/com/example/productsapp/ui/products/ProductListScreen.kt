package com.example.productsapp.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.productsapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onProductClick: (Int) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()
    var showSortMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.products)) },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            Icons.Default.RestartAlt,
                            contentDescription = stringResource(R.string.reset_local_changes)
                        )
                    }
                    Box {
                        TextButton(onClick = { showSortMenu = true }) {
                            Text(stringResource(R.string.sort))
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            listOf(
                                "default" to stringResource(R.string.sort_default),
                                "price_asc" to stringResource(R.string.sort_price_asc),
                                "price_desc" to stringResource(R.string.sort_price_desc),
                                "rating" to stringResource(R.string.sort_rating),
                                "title" to stringResource(R.string.sort_title)
                            ).forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.onSortChange(key)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showAddDialog) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_product)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_products)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true
            )

            if (categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.onCategorySelected(null) },
                            label = { Text(stringResource(R.string.all)) }
                        )
                    }
                    items(count = categories.size) { index ->
                        val category = categories[index]
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = { Text(category) }
                        )
                    }
                }
            }

            when {
                products.loadState.refresh is LoadState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                products.loadState.refresh is LoadState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.something_went_wrong))
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { products.retry() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products.itemCount) { index ->
                            products[index]?.let { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    onFavoriteClick = { viewModel.toggleFavorite(product.id) }
                                )
                            }
                        }

                        if (products.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddEditDialog) {
        AddEditProductDialog(
            product = uiState.editingProduct,
            onDismiss = viewModel::hideDialog,
            onSave = viewModel::saveProduct
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.reset_changes)) },
            text = { Text(stringResource(R.string.reset_changes_message)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetLocalChanges()
                    showResetDialog = false
                }) { Text(stringResource(R.string.reset)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}