# Shared Secret

Special assignment for Cryptography course at [ITBA](https://www.itba.edu.ar)

## Building

This framework uses [maven](https://maven.apache.org). To build it:

``` bash
$ git clone https://github.com/matifraga/cryptography.git master
$ cd cryptography/
$ mvn clean package
```

The compiled program will be located inside the `target/` folder with the name 
`
uber-cryptography-1.0-SNAPSHOT.jar
`.

## Running Arguments

The program supports the following arguments:

| Key   |      Value      |
|:----------:|:-------------:|
| d/r |  Distribute/Recovery |
| n |    Total number of shades   |
| k | Minimum number of shades needed |
| dir | Lookup directory for images |
| secret | Secret image file |

### Examples

Distribution:

``` bash
java -jar uber-cryptography-1.0-SNAPSHOT.jar -d -secret secret.bmp -k 3 -n 5 -dir hosts
```

Recovery:
``` bash
java -jar uber-cryptography-1.0-SNAPSHOT.jar -r -secret secret.bmp -k 3 -n 5 -dir dist
```
The dist directory must contain only the shades from the current run. If the directory contains shades from a previous run the secret won't be recovered.

## Credits

* [Marzoratti, Luis](https://github.com/lmarzora)
* [Soncini, Lucas](https://github.com/lsoncini)
* [Fraga, Matias](https://github.com/matifraga)
* [De Lucca, Tomas](https://github.com/tomidelucca)

</br>

![Raptor](http://files.tomidelucca.me/images/raptor-black-100.png)
