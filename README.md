# travic-test
Checked features from travis

### Travic Lifecycle 
Сборка проекта в Travis CI осуществляется в два этапа:
1. Установка всех зависимостей - install 
2. Запуск сборочного скрипта - script

Полный жизненный цикл сборки состоит из следующих этапов:
1. apt addons (Необязательное использование)
2. cache components  (Необязательно использование). 
```
Travis CI может кэшировать данные, которые не часто изменяется, дабы ускорить процесс сборки. 
Чтобы использовать кеширования, в настройках репозитория проекта в Travic CI установите Build branch updates -> ON
Кэширование позволяет Travis CI хранить каталоги между сборками. К примеру это удобно при сборке проекта, когда 
идет скачивания большого количества зависимостей для компиляции проекта.
Travic CI загружает кэш на сервер S3 после фазы "script", но до фаз after_success/after_failure. Если кэш не удалось 
загрузить, сборка не будет маркироваться как failed.
Вы можете кэшировать произвольные каталоги, такие как каталоги Gradle, Maven, Composer и npm, между сборками, 
перечисляя их в своем .travis.yaml
Если вы хотите выполнить какие-либо действия перед кэшированием данных, то это можно сделать в фазе before_cache.

cache:
  directories:
    - $HOME/.cache/pip
before_cache:
  - rm -f $HOME/.cache/pip/log/debug.log
  
Чтобы отключить использование кэширование:
cache: false

Таймаут у кэширование = 3 минуты. Если за три минуты не удалось закачать данные на сервер, то кэширование прерывается 
и продолжают выполнятся другие фазы. Но можно управлять этим параметров:
cache:
  timeout: 1000
  
Прошу обратить внимание, что загрузка кэша просиходит на другой сервер по защищенному каналу обеспечивая 
конфидециальность информации. Кэш связан с пропускной шириной канала, по-этому не рекомендуется сохранять данные 
более нескольких сотен мегабайт. Так же содержимое кэша доступно для любой сборки, включая Pull Request, по-этому 
не помещайте конфидициальную информацию в кеш.
Кэш хранится 30 дней при условии что сборка запускается на travis-ci.org 
 
```

###Собственные скрипты

Если у вас есть собственные скрипты в которых прописана сложная логика, то вы можете вызвать их
из файла .travis.yml
Рассмотрим пример сборки проекта с отдельными скриптами (Это только для сборок, а не для PR/MR)
```html
#!/bin/bash
set -ev
bundle exec rake:units
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	bundle exec rake test:integration
fi
```    
Команда set -ev, флаг -е говорит о том, что как только появиться ошибка выполнения, скрипт автоматически
перестанет выполняться. Это позволяет экономить время вашей сборки, а так же удобно использовать
эту команду в фазе инсталяции, чтобы завершить текущий скрипт и перейти к следующему. 
 
Флаш -v, напечатает строки скрипта до того как он выполниться. Удобно при отладке и поиска ошибок. 

Предположим ваш скрипт находится в scripts/run-tests.sh репозитория проекта. Установлены соответствущие права
на запуск chmod ugo+x scripts/run-tests.sh. Для вызова его необходимо в .travis.yml, прописать
```html
script: ./scripts/run-tests.sh
```

###Deployment
Travis CI предоставляет перечень провайдеров, которые позволяет загружать артифакты в определенные хранилища.
Список хранилищ:

    anynines
    Appfog
    Atlas
    AWS CodeDeploy
    AWS Elastic Beanstalk
    AWS Lambda
    AWS OpsWorks
    AWS S3
    Azure Web Apps
    bintray
    BitBalloon
    Bluemix CloudFoundry
    Boxfuse
    Catalyze
    Chef Supermarket
    Cloud 66
    CloudFoundry
    Deis
    Engine Yard
    GitHub Pages
    GitHub Releases
    Google App Engine
    Google Cloud Storage
    Google Firebase
    Hackage
    Heroku
    Launchpad
    Modulus
    npm
    OpenShift
    packagecloud.io
    Puppet Forge
    PyPI
    Rackspace Cloud Files
    RubyGems
    Scalingo
    Script
    Surge.sh
    TestFairy
  
Чтобы загрузить артифакты в другое хранилище, которое не представлено списком выше (либо произвести загрузку на свой сервер), 
необходимо использовать script, либо фазу after_success.

```html
env:
  global:
    - "FTP_USER=user"
    - "FTP_PASSWORD=password"
after_success:
    "curl --ftp-create-dirs -T uploadfilename -u $FTP_USER:$FTP_PASSWORD ftp://sitename.com/directory/myfile"
```
```html
after_success:
  - eval "$(ssh-agent -s)" #start the ssh agent
  - chmod 600 .travis/deploy_key.pem # this key should have push access
  - ssh-add .travis/deploy_key.pem
  - git remote add deploy DEPLOY_REPO_URI_GOES_HERE
  - git push deploy
```

Если для загрузки артифактов/развертывания требуется больше настроек, чем позволяет фаза after_success, используйте собственный скрипт.
В примере ниже выполняется сценарии scripts/deploy.sh в develop ветке, если сборка прошла успешно.

```html
deploy:
  provider: script
  script: scripts/deploy.sh
  on:
    branch: develop
```

###Uploading Files
Когда вы загружаете артифакты в хранилище, запретите Travis CI сбрасывать рабочий каталог, а так же 
удалять все изминения, которые были сделаны во время сборки. 
```html
deploy:
  skip_cleanup: true
```


###Одновременная заргрузка в различные хранилища
Travis CI позволяет одновременно загружать артифакты сборки в различные центральные репозитории (хранилища).
Чтобы воспользоваться данным способом, необходимо в фазе deploy прописать следующее:
```html
deploy:
  - provider: cloudcontrol //Указываем хранилище cloudControl 
    email: "YOUR CLOUDCONTROL EMAIL"
    password: "YOUR CLOUDCONTROL PASSWORD"
    deployment: "APP_NAME/DEP_NAME"
  - provider: heroku //Указываем хранилище Heroku
    api_key: "YOUR HEROKU API KEY"
``` 

###До