# Tim's Usercentrics Case Study
## UML
![Usercentrics CaseStudy drawio (1)](https://github.com/user-attachments/assets/f87be599-ca59-4ebb-bbed-b1c3a081ba68)
## Thought Process
### Intro
I love documenting my thought process because just displaying the code can lead to different interpretations. This text might seem long, but it's short and to the point taking you step by step presenting my solution to the case study.<br>
Enjoy! :)
### Planning
I approached the task as a developer of a client that wants to use Usercentrics in their App. My guide was to build a scalable app decoupled as much as possible from the implemented product. The most important rule I work by is the single responsibility approach, where each class must have only one logic to perform. This ensures modular ability, testability and readability.
Having all that in mind, I came across two interesting approaches that shaped this project:
#### Separating the SDK from the App
The best approach is to wrap the Usercentrics SDK in a class that implements an interface with common actions the app requires. The benefits are:
- If different regions have different products or different companies, an interface declares the same actions but with different implementations. All we need is a different class with the same implementation.
- Any changes in the SDK's method signature or initialization will be tackled in a wrapped class without affecting the rest of the application.
- As the complexity of the product grows, it will be easier to test a mocked version of the user consent without actually calling the SDK.
#### Separating the SDK context requirement from the Activity \ ViewModel
The Usercentrics API requires us to pass an Activity's context to display the consent page. However, it also requires the response callback to be attached as well. This creates an issue where the Activity is aware of the response instead of passively observing changes from the ViewModel. The naive approach will be to link a ViewModel's method to the activity's display method call:
```kotlin
  userConsentManager.display(this, userConsentCostViewModel::onBannerResponse)
```
It's a good approach, but if we want to do an action before displaying the banner (like showing a loader), we're in trouble, because then we will need to inform the ViewModel (since the Activity must be passive):
```kotlin
  userConsentCostViewModel.onPreBannerDisplay()
  userConsentManager.display(this, userConsentCostViewModel::onBannerResponse)
```
Alternatively, we can just pass the context to the ViewModel itself:
```kotlin
  Activity {
    fun showBanner() {
      userConsentCostViewModel.displayBanner(context)
    }
  }

  ViewModel {
    fun displayBanner(context: Context) {
      userConsentManager.display(context, ::onBannerResponse)
    }
  }
```
These two approaches break abstraction. Thinking about applications being scalable and complex teaches me that such an approach leads to other similar methods resulting in code that is hard to read and maintain.<br>
The solution then, was to use an API that allows us to observe the current Activities lifecycle. Because only one Activity can be in `onResume` at any given time, we store its reference in a wrapper of WeakReference (also solving any potential memory leaks). This ensures that the Usercentrics wrapper doesn't rely anymore on an external context and can be called from the ViewModel.
## Execution
After resolving these two interesting questions, the app follows a typical MVVM approach with Koin for Dependency Injection. The calculations are stored in a separate class that is also abstracted from Usercentrics, which allows for easy Unit Testing according to the attached test case studies.<br>
I also separated the settings id key provided to me from the app into the local.properties file, since this is a public repository and I am not certain about sharing the key with outsiders. To make the project work add to your `local.properties` your own Usercentrics settings id:
```
  usercentrics.SettingsId="your_settings_id"
```
The project was compiled and ran on the latest version of Android Studio.
