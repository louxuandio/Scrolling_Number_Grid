package com.example.scrollingnumbergrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scrollingnumbergrid.ui.theme.ScrollingNumberGridTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.focus.focusModifier


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrollingNumberGridTheme (
            ){
                NumberGrid()
                //TODO:LazyVerticalGrid with 3 columns
                //TODO:use coroutines with a delay to generate 30 more numbers when scrolling
                //TODO:add a reset button
            }
        }
    }
}

@Composable
fun NumberGrid(){
    var loading = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    //I tried a lot of way for LaunchedEffect, but they all didn't work.
    //At last I found gridState with the help of ChatGPT
    val numbers = remember { mutableStateListOf(*((1..30).toList().toTypedArray())) }
    //ChatGPT teaches me how to remember the numbers and why.

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(numbers){ number ->
            Box(modifier = Modifier.fillMaxWidth().background(Color.LightGray).height(80.dp), contentAlignment = Alignment.Center){
                Text(
                    text = number.toString(),
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
        //I did some research for the items and item.
        item {
            LaunchedEffect(gridState) {
                loading.value = true
                delay(1000)
                loadMore(numbers.size, scope){newNum ->
                    numbers.addAll(newNum)
                    loading.value = false
                }
            }
        }

        /*
        //This one doesn't work
        if (!loading.value) {
            item {
                LaunchedEffect(gridState) {
                    loading.value = true
                    delay(1000)
                    loadMore(numbers.size, scope){newNum ->
                        numbers.addAll(newNum)
                        loading.value = false
                    }
                }
            }
        }
    }

    //This one works, but the snapshotFlow was written by ChatGPT, and I don't know what that is.
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= numbers.size - 1 && !loading.value) {
                    loading.value = true
                    delay(1000)
                    val newNumbers = (numbers.size + 1..numbers.size + 30).toList()
                    numbers.addAll(newNumbers)
                    loading.value = false
                }
            }
    }
    //This one doesn't work either. I think that's because of loading.value
    if (!loading.value){
        item {
            LaunchedEffect(numbers.size) {
                loading.value = true
                delay(1000)
                loadMore(numbers.size, scope){newNum ->
                    numbers.addAll(newNum)
                    loading.value = false
                }
            }
        }
    }

    //This is ChatGPT teaching me how to find what's wrong
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                println("Last visible item index: $lastVisibleItemIndex")
                if (!loading.value && lastVisibleItemIndex == numbers.size - 1) {
                    println("Triggering loadMore...")
                    loading.value = true
                    delay(1000)
                    val newNumbers = (numbers.size + 1..numbers.size + 30).toList()
                    println("Adding numbers: $newNumbers")
                    loadMore(numbers.size, scope) {
                        numbers.addAll(newNumbers)
                        loading.value = false
                    }
                }
            }

         */
    }
    FloatingActionButton(
        onClick = { numbers.clear();numbers.addAll(1..30) },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("reset")
    }
}
//Written ith the help of ChatGPT.
//I asked ChatGPT how does the onNewData works.
fun loadMore(currentSize: Int, scope: CoroutineScope, onNewData: (List<Int>) -> Unit){
    scope.launch {
        //delay(1000)
        val newNum = (currentSize+1..currentSize+30).toList()
        onNewData(newNum)
    }
}
