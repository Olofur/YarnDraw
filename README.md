# Yarndraw

Program for drawing patterns on a rectangular grid

## Table of Contents
-----------------

* [Overview](#overview)
* [Features](#features)
* [Installation](#installation)
* [Usage](#usage)
* [Contributing](#contributing)
* [License](#license)

## Overview
------------

This project aims to build a graphical program for drawing patterns on a square grid. There are many possible uses for such a program, but the goal here was to make one suitable for drawing embroidery patterns. 

One important aspect in such an application is being able to represent each grid square with not only a color, but also a symbol, since these are much easier to grasp when working with the pattern. 

## Features
------------

* Key features include:
	+ Coloring grid squares in RGB (#RRGGBB) format. This comes with some standard painting program tools, like
		- dragging the mouse to color several squares at a time
		- swapping all grid squares of a certain color with another
		- filling in color in an area

	+ Switching between representing each grid square with a color, symbol or both. The project includes a few svg files that are used to make out these symbols, and it is really easy to add more yourself using e.g. InkScape.

	* (To be added) Save as svg file.

	+ (To be added) Printing support.

 GUI structure : 

 ColorPanel[]    gridPanels
    |
    V
 JPanel          clickableGridPanel
    |
    V
 JScrollPane *   zoomableClickableGridPanel
    |
    V
 JTabbedPane *   zoomableClickableGridPanelTabs
    |
    V
 JSplitPane      mainPane
    |
    V
 JFrame          frame

## Installation
------------

### Prerequisites

The following dependencies where used by me when building the project. It is also possible that earlier versions of these work fine.

+ **Java 17**

+ **Apache Maven 3.6.3**

+ **org.apache.xmlgraphics 1.14** 
	- The project uses xmlgraphics to make use of its vector image functionalities

### Installation Steps

1. Clone the repository: `git clone https://github.com/Olofur/YarnDraw.git`

2. Install dependencies: `mvn install`

3. Build the project: `mvn package`

## Usage
-----

how to use 

### Example Usage


### Bug Reports

+ Open an issue on GitHub issues
+ Provide a detailed description of the bug, including steps to reproduce

### Feature Requests

+ Open an issue on GitHub issues
+ Provide a detailed description of the feature request

#### Current TODO

	[X] - Dra med musen för att färglägga flera pixlar
	[X] - Välj mellan färg- och symbolrepresentation
	[X] - Ändra alla pixlar av en färg
	[X] - Fyll i ett område med en färg
	[X] - Möjliggör att göra rutnät större än 100x100
	[X] - Ha flera flikar med rutnät
	[ ] - Gör möjligt att dra in menypanelen åt höger / ta bort färghjulet

	[X] - Zooma 
	[X] - Spara mönster
	[X] - Ladda mönster
	[ ] - Skriv ut på ett bra sätt

	[ ] - Dokumentation
	[ ] - Dockerize

## License
-------

The project is released on an MIT licence
