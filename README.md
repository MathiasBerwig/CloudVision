# Cloud Vision Demo

Este arquivo também está disponível em [português](README-PT_BR.md).

## About the App

Cloud Vision Demo is an Android (4.4+) app that demonstrates the [Cloud Vision API](https://cloud.google.com/vision/) in a simple and well documented way. With the support of the query APIs of  [Wikipedia](https://pt.wikipedia.org/w/api.php) and [Wikidata](https://query.wikidata.org/), it shows additional info about the logo and landmarks identified in the image, besides a list of the related tags.

It was developed by me with some simple purposes in mind: learn a bit more about computer vision; fill my free time creating an Android app; know better the [Google Cloud Platform](https://cloud.google.com/); and practice my english writing skills (nothing better than do documentation, right!?). As the app was taking form, my interest in show it for more people increased, so I decided to make it open source and write academic papers about this subject.

## Screenshots
TODO: Add some screenshots

## How to set it up

The Cloud Vision Demo uses two Google Cloud Platform APIs that require authentication keys: the [Maps](https://developers.google.com/maps/documentation/android-api/) and [Cloud Vision](https://cloud.google.com/vision/). The process to get it is very simple, you just need to open the [Google Console Developers](https://console.developers.google.com/), create a new project and activate both APIs, so generate the server key. If you doesn't know this procedure, see the [help](https://support.google.com/cloud/).

Now you can add the key to the file  `google_apis.xml` in `CloudVision\app\src\debug\res\values\`. After this,  just compile the app in debug mode.

## Dependencies

 - [MaterialSheetFab](https://github.com/gowong/material-sheet-fab)
 - [MaterialViewPager](https://github.com/florent37/MaterialViewPager)
 - [HTextView](https://github.com/hanks-zyh/HTextView)
 - [Android-RoundCornerProgressBar](https://github.com/akexorcist/Android-RoundCornerProgressBar)
 - [OkHttp](https://github.com/square/okhttp)
 - [AppIntro](https://github.com/PaoloRotolo/AppIntro)
 - [Glide](https://github.com/bumptech/glide)
 - [AndroidSVG](https://github.com/BigBadaboom/androidsvg)
 - [Calligraphy](https://github.com/chrisjenx/Calligraphy)
 - [android-gif-drawable](https://github.com/koral--/android-gif-drawable)

## License

This app is licensed under [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

See [`LICENSE`](LICENSE) to the full statement.

    Copyright (C) 2015 [Mathias Berwig](https://github.com/MathiasBerwig).
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.