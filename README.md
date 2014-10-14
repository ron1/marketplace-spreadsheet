# Nuxeo Spreadsheet

## About

This project allows to build the Marketplace package for the
`nuxeo-platform-spreadsheet` addon.

This addon allows bulk editing documents starting from a content view result set,
providing a spreadsheet-like user experience.
      
For further information please read the [documentation](http://doc.nuxeo.com/x/lQ45AQ).

## Building

To build and run the tests, simply start the Maven build:

    mvn clean install

To run functional tests:

    mvn clean install -Pftest

## Installing

To install the package:

 1. Take a fresh Nuxeo CAP (>= 5.9.4).

 2. Install the nuxeo-spreadsheet package:
      - From the AdminCenter (Upload + install)
      - From the command line using `nuxeoctl mp-install package.zip`