package com.example.myapplication.ui.movie

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.model.MovieModel
import com.example.myapplication.utils.NetworkImage
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MovieScreen(modifier: Modifier = Modifier) {
    val mainViewModel = hiltViewModel<MovieViewModel>()
    val firstPageLoading by mainViewModel.firstPageStateFlow.collectAsState()
    val loadMoreLoading by mainViewModel.loadingStateFlow.collectAsState()
    val dataMovie by mainViewModel.movieStateFlow.collectAsState()
    val errorMessage by mainViewModel.errorMessageStateFlow.collectAsState()
    var showDialog by remember {mutableStateOf(false)}
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            if (errorMessage.isNotEmpty()) {
                Snackbar(
                    action = {}, modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = errorMessage)
                }
            } else {
                it.currentSnackbarData?.dismiss()
            }
        }, topBar = {
            TopAppBar(title = { Text("Film list") }, actions = {
                IconButton(onClick = {showDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        modifier = Modifier,
                        contentDescription = null
                    )
                }
            }
            )
        }

    ) { values ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(values),
            color = MaterialTheme.colors.background
        ) {
            if (firstPageLoading) {
                ItemLoading(modifier = Modifier.fillMaxSize())
            } else {
                if (dataMovie.isNotEmpty()) {
                    MovieList(
                        stateLoadMore = loadMoreLoading,
                        movies = dataMovie,
                        loadNextPage = mainViewModel::loadMore
                    )
                }
            }
            if (showDialog) {
                InputDialogView(onDismiss = {
                             showDialog = false
                }, onSearch = {
                    showDialog = false
                    mainViewModel.searchFilm(it)
                })
            }
        }
    }
}

@Composable
fun MovieList(
    stateLoadMore: Boolean,
    movies: List<MovieModel>,
    loadNextPage: () -> Unit,
) {
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(all = 8.dp),
    ) {
        items(
            count = movies.size + 1,
            key = { movies.getOrNull(it)?.imdbID ?: "PLACEHOLDER" },
        ) { index ->
            val movie = movies.getOrNull(index)
            if (movie != null) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                ) {
                    NetworkImage(
                        url = movie.poster,
                        contentDescription = null,

                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.5f)
                    )
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
                return@items
            }
            if (stateLoadMore) ItemLoading()

        }
    }
    InfiniteListHandler(listState = listState) {
        loadNextPage()
    }
}

@Composable
fun InfiniteListHandler(
    listState: LazyGridState,
    buffer: Int = 3,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect {
                onLoadMore()
            }
    }
}

@Composable
fun ItemLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun InputDialogView(onDismiss:() -> Unit, onSearch:(String) -> Unit) {
    val context = LocalContext.current
    var searchedFood by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier.padding(8.dp),
            elevation = 8.dp
        ) {
            Column(
                Modifier
                    .background(Color.White)
            ) {
                Text(
                    text = stringResource(id = R.string.title_dialog),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 20.sp
                )
                OutlinedTextField(
                    value = searchedFood,
                    onValueChange = { searchedFood = it }, modifier = Modifier.padding(8.dp),
                    label = { Text(stringResource(id = R.string.hint_search)) }
                )
                Row {
                    OutlinedButton(
                        onClick = {  },
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1F)
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = {
                            onSearch(searchedFood) },
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1F)
                    ) {
                        Text(text = "Search")
                    }
                }
            }
        }
    }
}