#!/bin/bash

rm -r minipack
mkdir minipack
mkdir lib

mvn clean install

cp target/core-0.0.1-SNAPSHOT.jar minipack/lib/flens.jar
cp target/dependency/* minipack/lib

pushd ../plugins/flens-es
mvn clean install
cp target/flens-es-0.0.1-SNAPSHOT.jar ../../core/minipack/lib
cp target/dependency/*                ../../core/minipack/lib
popd

pushd ../esper/
mvn clean install
cp target/esper-0.0.1-SNAPSHOT.jar    ../core/minipack/lib
cp target/dependency/*                ../core/minipack/lib
popd

cd minipack

zip -cRf flens.zip *
