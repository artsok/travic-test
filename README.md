# travic-test
Checked features from travis

### Travic Lifecycle 
Сборка проекта в Travis CI осуществляется в два этапа:
1. Установка всех зависимостей - install 
2. Запуск сборочного скрипта - script

Полный жизненный цикл сборки состоит из следующих этапов:
1. apt addons (Необязательное использование)
apt настроен так, чтобы не требовать подтверждения (флаг -y задан по умолчанию). 
Это означает, что apt-get install -qq может использоваться без флага -y.
```html
before_install:
  - sudo apt-get -qq update
  - sudo apt-get install -y libxml2-dev
```

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

###Дополнительные условия с оператором on:
Загрука может контролироваться с помощью условия 'on' для каждого хранилища.
Когда все условия, указанные в разделе 'on', будут выполнены, тогда произойдет загрузка артефактов.

```html
deploy:
  provider: s3
  access_key_id: "YOUR AWS ACCESS KEY"
  secret_access_key: "YOUR AWS SECRET KEY"
  bucket: "S3 Bucket"
  skip_cleanup: true //Пропустить очистку
  on: //Условие
    branch: release
    condition: $MY_ENV = super_awesome
``` 

В условие 'on' мы можем задавать параметры
1. repo - Путь до вашего репозитория (owner_name/repo_name, т.е, travis-ci/dpl).
2. branch - Название ветки. Если это название опущено, то по дефолту это либо master, либо иная специфичная ветка приложения.
Если вам не известно название ветки, вы можете объявить параметр 'all_branches: true' вместо 'branch: **'
3. jdk, node, perl, php, python, ruby, scala, go: Можно указать определенную версию, на которой будет выполняться загрузка артефактов
4. condition: Можно указывать bash условия. Выражение должно быть строковым литералом. if [[ <condition> ]]; then <deploy>; fi
Выражение может быть составляющим, но оно должно быть только одно. К примеру '$CC = gcc'
5. tags: Когда данный параметр выставлен в true, артефакты приложения будут загружены если в комиите был установлен
tag. Данное условие приводит к игнорированию условия branch 

Ниже пример демонстрирует загрузку артефактов в хранилище Appfrog, только с ветки staging, при условии,
что тесты будут запущены на node.js версии 0.11

```html
deploy:
  provider: appfog
  user: ...
  api_key: ...
  on:
    branch: staging
    node: '0.11' # this should be quoted; otherwise, 0.10 would not work
```

Далее пример, демонстрирует загрузку артефактов в хранилизе S3, при условии, что переменная $CC установлена в
gcc.
```html
deploy:
  provider: s3
  access_key_id: "YOUR AWS ACCESS KEY"
  secret_access_key: "YOUR AWS SECRET KEY"
  skip_cleanup: true
  bucket: "S3 Bucket"
  on:
    condition: "$CC = gcc"
```

Далее пример демонстрирует загрузку артифактов на GitHub при включенном tag и версия Ruby 2.0.0
```html
deploy:
  provider: releases
  api_key: "GITHUB OAUTH TOKEN"
  file: "FILE TO UPLOAD"
  skip_cleanup: true
  on:
    tags: true
    rvm: 2.0.0
```

Если вы не нашли в списке работы с нужным хранилищем, вы может запросить службу поддержки (support@travis-ci.com)
добавить хранилище, либо сделать индивидуальную загрузку с помощью фазы after-success или script provider.
Так же можно и самому поэксперементировать (https://github.com/travis-ci/dpl) с созданием механизма загрузки в нужное хранилище,
для этого необхоимо указать параметр edge
```html
deploy:
  provider: awesome-experimental-provider
  edge: true
```

Запомните, что сборки с PullRequests пропускают фазу deployment.  


#####Загрузка артефактов с помощью фазы after-success, в которой прописываем нужные действия
```html
env:
  global:
    - "FTP_USER=user"
    - "FTP_PASSWORD=password"
after_success:
    "curl --ftp-create-dirs -T uploadfilename -u $FTP_USER:$FTP_PASSWORD ftp://sitename.com/directory/myfile" //Загрузка файла на ftp сервер
``` 

Пользователь и пароль, могут быть зашифрованы. 
```html
gem install travis
travis encrypt SOMEVAR="secretvalue"
```

Так же ниже пример загрузки артифактов на сервис через git
```html
after_success:
  - eval "$(ssh-agent -s)" #start the ssh agent
  - chmod 600 .travis/deploy_key.pem # this key should have push access
  - ssh-add .travis/deploy_key.pem
  - git remote add deploy DEPLOY_REPO_URI_GOES_HERE
  - git push deploy
```

#####Загрузка артефактов с помощью script deployment
Для более гибкой настройки загрузки артифактов, вы можете использовать свои скрипты. Следующий пример запускает скрипт
распаложенный scripts/deploy.sh для ветки branch при условии успешной сборки.

```html
deploy:
  provider: script
  script: scripts/deploy.sh
  on:
    branch: develop
```
Если скрипт вернет ненулевой статус, deploy будет неудачным, а сборка (build) будет помечена, как ошибка "error".

##### Передача аргументов в пользовательский скрипт
```html
eploy:
  # deploy develop to the staging environment
  - provider: script
    script: scripts/deploy.sh staging //Передаем аргумент staging 
    on:
      branch: develop
  # deploy master to production
  - provider: script
    script: scripts/deploy.sh production //Передаем аргумент production
    on:
      branch: master
``` 

Так же в скрипт можно передавать переменным среды (environment variables).
```html
deploy:
  provider: script
  script: scripts/deploy.sh production $TRAVIS_TAG //Передача переменной
  on:
    tags: true
    all_branches: true
```



##Environment Variables
Часто в сборке используются переменные среды, которые могут быть использованы на любой стадии сборки.


###VM Images
Travic CI поддерживает виртуальные среды окружения в которых запускаются сборки. Каждая
сборка выполняется в одной из следующих виртуальных сред:
1. Sudo-enabled: Полная поддержка sudo. Используются дистрибутивы Linux Ubuntu Precise 12.04 или Ubuntu Trusty 14.04;
2. Container-based: Быстрая загрузка виртуальной среды. Команда sudo отключена. Используются дистрибутивы Ubuntu Trusty 14.04;
3. OS X: Дистрибутивы для ОС Mac;

 

 