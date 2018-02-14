# AgendaView

[![](https://jitpack.io/v/jdmbotero/AgendaView.svg)](https://jitpack.io/#jdmbotero/AgendaView)

This library that allows you to generate a calendar with each day, time, entry and consultation of events.

Usage
===============================

Grab it from jitPack:

```groovy
    compile 'com.github.jdmbotero:agendaview:1.0.1'
````

Declare this view in your layout like below.

```java
    <com.github.jdmbotero.agendaview.AgendaView
        android:id="@+id/agendaView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
````


Customization
===============================

You can set this properties from xml code


| Property                | Description |
| ---                     | ---         |
| `firstDay`              | ---         |
| `numberOfDays`          | ---         |
| `hourHeight`            | ---         |
| `backgroundColor`       | ---         |
| `dayTextColor`          | ---         |
| `dayCurrentColor`       | ---         |
| `dayCurrentTextColor`   | ---         |
| `hourTextColor`         | ---         |
| `hourCurrentColor`      | ---         |
| `dayBackground`         | ---         |
| `daySelectedBackground` | ---         |
| `showNewEventInClick`   | ---         |
| `newEventTimeInMinutes` | ---         |
| `newEventColor`         | ---         |
| `newEventText`          | ---         |
| `newEventTextColor`     | ---         |
| `allowNewEventPrevNow`  | ---         |



License
===============================

    Copyright 2018 Daniel Morales

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.