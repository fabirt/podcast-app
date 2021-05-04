# Podcast App

Android podcast app made with Jetpack Compose and ExoPlayer.

Podcast information provided by [Listen Notes API](https://www.listennotes.com/).

## Features

- Jetpack Compose UI. Custom animations, transitions, light/dark theme, and layouts.
- Jetpack Compose Navigation.
- Dependency injection with Hilt.
- MVVM Architecture.
- Retrieves podcasts metadata from the network.
- Allows background playback using a foreground service.
- Media style notifications.
- Uses a `MediaBrowserService` to control and expose the current media session.
- Controls the current playback state with actions such as: play/pause, skip to next/previous, shuffle, repeat and stop.
- Supports offline playback using `CacheDataSource` from `ExoPlayer`.
- Process images to find its color palette using Palette API.

## Libraries

- Jetpack Compose
- ExoPlayer
- Glide
- Hilt
- Retrofit
- Navigation
- ViewModel
- DataStore
- Palette API

## Result

### Dark Mode
| ![welcome](demo/welcome_dark.png) | ![podcasts](demo/home_dark.png) |![detail](demo/detail_dark.png) |![player](demo/player_dark.png) |
|----------|:-------------:|:-------------:|:-------------:|

### Light Mode
| ![welcome](demo/welcome_light.png) | ![podcasts](demo/home_light.png) |![detail](demo/detail_light.png) |![player](demo/player_light.png) |
|----------|:-------------:|:-------------:|:-------------:|

### Demo
![player](demo/listen-notes-demo.gif)