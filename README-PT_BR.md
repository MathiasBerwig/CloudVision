# Cloud Vision Demo

This file is also available in [english](README.md).

## Sobre o aplicativo

O Cloud Vision Demo é uma aplicação para Android (4.4+) que demonstra o funcionamento do [Cloud Vision API](https://cloud.google.com/vision/) de forma simples e bem documentada. Com o auxílio das APIs de busca do [Wikipedia](https://pt.wikipedia.org/w/api.php) e [Wikidata](https://query.wikidata.org/), ele exibe informações adicionais sobre logotipos e marcos identificados na imagem, além de uma lista com as tags relacionadas.

Ele foi desenvolvido por mim com alguns propósitos simples em mente: aprender um pouco mais sobre visão computacional; ocupar meu tempo livre criando um app para Android; conhecer melhor o [Google Cloud Platform](https://cloud.google.com/); e aprimorar minha escrita em inglês (nada melhor do que fazer documentação). A medida que o app foi tomando forma, meu interesse em mostra-lo à mais pessoas aumentou, por isso decidi torna-lo open source e escrever artigos acadêmicos sobre o tema. 

## Capturas de tela
TODO: Adicionar capturas de tela

## Como configurar

O Cloud Vision Demo utiliza duas APIs do Google Cloud Platform que exigem chaves de autenticação: o [Maps](https://developers.google.com/maps/documentation/android-api/?hl=pt-br) e [Cloud Vision](https://cloud.google.com/vision/). O processo para obtê-las é bem simples, basta acessar o [Google Console Developers](https://console.developers.google.com/), criar um novo projeto e ativar ambas as APIs, para então obter a chave de servidor. Caso esse procedimento seja novo para você, veja a [sessão de ajuda](https://support.google.com/cloud/).

Agora você deve adicionar a chave de acesso ao arquivo `google_apis.xml` em `CloudVision\app\src\debug\res\values\`. Após isso, basta compilar e executar o app em modo debug.

## Dependências

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

## Licença

Este aplicativo está licenciado sob [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Veja [`LICENSE`](LICENSE) para o arquivo completo (em inglês).

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