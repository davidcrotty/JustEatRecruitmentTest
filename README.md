

# How long did you spend on the coding test? What would you add to your solution if you had more time? If you didn't spend much time on the coding test then use this as an opportunity to explain what you would add.
* Time spent: 3 hours
* Unit tests for logic in VM and Repo
* Composable tests to verify behaviour of screen
* Snapshot UI tests to prevent UI regressions
* Snapshot tests for backend to have early warning on any backend API changes and keep documentation on expected behaviour for version.
* DI Framework, manual DI won't scale due to classes being created increasing as application grows
* Add a datasource to decouple technologies from the Repository (should keep it for mediation and core business logic policy).
* Add cases for location finding failure (ie lat/long being null, timeout for no location)
* Extract hard coded values into strings and dimens for localisation and re-use
* Error handling, logging, traceability and observability
* Consider multi module depending on scope of application, App would compose other modules via abstraction (ie: Repo interface would live in domain module, a feature module for PostCode would be created and may implement said interface)

# What was the most useful feature that was added to the latest version of your chosen language? Please include a snippet of code that shows how you've used it.
* Value class feature. Inlines classes to their primitives whilst also protects against the primitive obsession codesmell.
```
@JvmInline
value class PostCode(val text: String)
```

* Compose inner functions, allowing ability to scope functions in other functions to communicate their visibility
```
@Composable
fun PostCodeScreen(viewModel: PostCodeViewModel) {

    @Composable
    fun PostCodeForm(modifier: Modifier, state: PostCodeUIState, value: String, postCode: MutableState<String>, textChanged: (String) -> Unit) {
     // impl
    }

    PostCodeForm(Modifier, state = state, value = postCode.value, postCode = postCode, textChanged = {
        postCode.value = it
    })
}
```

# How would you track down a performance issue in production? Have you ever had to do this?
* Google play console for startup times. Can implement has drawn functionality to define when the app has started up manually.
* ANRs google play console
* Performance SDK (ie: Firebase performance) Add traces for scenarios, ie vm fetch starts trace, ends trace on result.
* Out of the box measures endpoint latency to explore if within SLAs for backend
* Use backend tooling like log explorer to export and query trends over time for latency.

# How would you improve the Just Eat APIs that you just used?
* https on logo urls
* Url width/height query params