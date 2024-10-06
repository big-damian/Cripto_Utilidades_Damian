<p align="center">
  - This readme is uncompleted, work in progress -
</p>

<p align="center">
  <img src="./app/src/main/res/drawable/damian_bitcoin_utils_icono_v2_cutre_redondo.png" alt="Cripto_Utilidades_Damian" width="110" />
</p>

<div align="center">

![App_stability-strong](https://img.shields.io/badge/App_stability-strong-green.svg)
![Language_used-Java](https://img.shields.io/badge/Language_used-Java-orange.svg)

</div>

# Cripto Utilidades Damian, a crypto Android application for my TFG (Bachelor´s Degree Final Project)

Cripto Utilidades Damian is an Android app developed as part of my Bachelor’s Degree Final Project (TFG). It provides users with a list of up-to-date cryptocurrencies and basic data using the CoinGecko API, along with detailed information based on the CoinMarketCap website. Users can add cryptocurrencies from the updated CoinGecko list to their wallet to track their value in euros. Additionally, you can log in to your account and update your user information.

## App Details

## User Stories

- [x] The user can see the list of cryptocurrencies from the CoinGecko API and see some basic information (using a RecyclerView).
- [x] Progressively load more list items (and their icon) by scrolling down the list (Using dynamic loading through API and image load with Glide).
- [x] The user can see updated info about the cryptocurrencies through a WebView connected to CoinMarketCap(https://coinmarketcap.com/).
- [x] The user can save his own crypto into a wallet to track their value (Using SQLite).
- [x] The user can log into his account, create a new one and edit some basic user data once he has logged in (Using a online MySQL database).
- [x] The user can backup his crypto to his account through the cloud and restore them (Using SQLite and MySQL databases).
- [x] User's email and password will be remembered in the login screen if "remember password is selected" (SQLite).
- [x] The user can see how many days are left until first of September (TFG delivery date) in the home screen.
- [ ] The user can see 3 crypto currencies in the home screen.
- [ ] The user can stay logged in if "remember password is selected".
- [ ] The user can search crypto in the crypto list.

### Screenshots

  |                           Home screen                            |                    Market screen (Crypto List)                     |                      Wallet screen (Tracking)                      |                             Account screen                               |
  |:----------------------------------------------------------------:|:------------------------------------------------------------------:|:------------------------------------------------------------------:|:------------------------------------------------------------------------:|
  | <img width="250" src="./README/screenshots/Home_screenshot.png"> | <img width="250" src="./README/screenshots/Market_screenshot.png"> | <img width="250" src="./README/screenshots/Wallet_screenshot.png"> | <img width="250" src="./README/screenshots/Login_Signup_screenshot.png"> |

### User Registration

  |                             Log in + sign in                             |                              Logged in                               |
  |:------------------------------------------------------------------------:|:--------------------------------------------------------------------:|
  | <img width="250" src="./README/screenshots/Login_Signup_screenshot.png"> | <img width="250" src="./README/screenshots/Loggedin_screenshot.png"> |

## Libraries Used

* [Data Binding] - Declaratively bind observable data to UI elements.
* [ViewModel] - Store UI-related data that isn't destroyed on app rotations. Easily schedule
* [SQLiteOpenHelper] - Access your app's SQLite database.
* [MySQL (java.sql -> DriverManager)] - To connect to remote MySQL database for login
  asynchronous tasks for optimal execution.
* [Volley] for making HTTP requests
* [Glide] for image loading
* [Retrofit] for dynamic loading a recyclerview from api (combined with Glide for images)

## Useful links and resources
- [Example link](https://github.com/big-damian/Cripto_Utilidades_Damian)
