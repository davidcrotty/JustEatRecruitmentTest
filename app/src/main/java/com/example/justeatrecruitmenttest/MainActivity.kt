package com.example.justeatrecruitmenttest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.justeatrecruitmenttest.di.PostCodeModule
import com.example.justeatrecruitmenttest.frameworks.LocationFetcher
import com.example.justeatrecruitmenttest.presentation.PostCode
import com.example.justeatrecruitmenttest.presentation.PostCodeUIState
import com.example.justeatrecruitmenttest.presentation.PostCodeViewModel
import com.example.justeatrecruitmenttest.presentation.ResturantModel
import com.example.justeatrecruitmenttest.ui.PostCodeScreen
import com.example.justeatrecruitmenttest.ui.theme.JustEatRecruitmentTestTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.concurrent.Executors

class MainActivity : ComponentActivity(), LocationFetcher {

    private val module = PostCodeModule()
    private val geoCoder by lazy { Geocoder(this) }

    private val postCodeViewModel by lazy {
        ViewModelProvider(
            this,
            module.viewModelFactory(this)
        ).get(PostCodeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JustEatRecruitmentTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostCodeScreen(postCodeViewModel)
                }
            }
        }
    }

    override fun requestLocation(callback: (Result<PostCode>) -> Unit) {
        val client = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        client.requestLocationUpdates(request, Executors.newSingleThreadExecutor(), object :
            LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lifecycleScope.launch(Dispatchers.Default) {
                    val lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        val result = geoCoder.getFromLocation(
                            lastLocation.latitude,
                            lastLocation.longitude,
                            1
                        )
                        withContext(Dispatchers.Main) {
                            if (result?.isEmpty() == true) {
                                callback(Result.failure<PostCode>(Exception("Postcode not found")))
                            } else {
                                val firstAddress = result?.first()
                                val postCode = firstAddress?.postalCode
                                if (postCode == null) {
                                    callback(Result.failure<PostCode>(Exception("No address found")))
                                } else {
                                    callback(Result.success(PostCode(postCode)))
                                }
                            }
                        }
                    }
                }
                client.removeLocationUpdates(this)
            }
        })
    }
}

