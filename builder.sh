#!/usr/bin/env sh
set -e
cd $(dirname $0)
. ./builder.conf

# FUNCTIONS
print(){ printf "$@"; }
println(){ printf "$@\n"; }

build(){
  ./mvnw clean package
}

deploy_local(){
  ./mvnw clean install -DskipTests=true
}

deploy_remote(){
  ./mvnw clean deploy -P deploy-sources,use-sonatype-stagging-repo -DskipTests=true
}

bump_version(){
  print "New version: "
  read -r VERSION

  VERSION=$(print $VERSION | sed '/\([0-9]\{1,3\}\)\.\([0-9]\{1,3\}\)\.\([0-9]\{1,3\}\)/!d')
  if [ "$VERSION" = "" ]; then
    println "Please use semantic version."
    exit 2
  fi
  
  POM_FILES=$(find . -name 'pom.yml')

  sed -i -e "s/version: \"$CURRENT_VERSION\"/version: \"$VERSION\"/" $POM_FILES
  println "CURRENT_VERSION='$VERSION'" > ./builder.conf
}

config_gpg(){
  if [ "$GPG_SIGNING_KEY" = "" ]; then
    println "ERROR: No GPG_SIGNING_KEY defined"
    exit 200
  fi

  mkdir -p ~/.gnupg/
  print "${GPG_SIGNING_KEY}" | base64 --decode > ~/.gnupg/private.key
  gpg --import ~/.gnupg/private.key
}

config_maven(){
  if [ "$OSSRH_USERNAME" = "" -o "$OSSRH_PASSWORD" = "" ]; then
    println "ERROR: Variables OSSRH_USERNAME or OSSRH_PASSWORD not defined"
    exit 201

  fi

  cat <<EOF> ~/.m2/settings.xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>${OSSRH_USERNAME}</username>
      <password>${OSSRH_PASSWORD}</password>
    </server>
  </servers>
</settings>
EOF
}

# MAIN
case $1 in
  "bump") bump_version ;;
  "build") build ;;
  "deploy_local"|"local") deploy_local ;;
  "deploy"|"remote") deploy_remote ;;
  "config_maven") config_maven ;;
  "config_gpg") config_gpg ;;
  *)
    cat <<EOF | sed 's/^[ \t]*//'
      Usage: $0 <OPTION>

      Where OPTION is one of the following:
      - build
      - bump - bumps the version
      - local - deploys generated artifacts locally
      - remote - deploys generated artifacts into Maven Central
      - deploy (same as 'remote')
      - deploy_local (same as 'local')
      - config_maven - configure Maven to publish into Sonatype Staging Repository
      - config_gpg - configure GPG key from the env variable

EOF
    exit 1
  ;;
esac
