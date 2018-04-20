# ghplayertracker

## Help wanted! No coding required!
Only the starting classes have been implemented and unfortauntly I don't have enough time to create the class specific attack modifier cards for them.

If you'd like to help all you need to do is the following:
* Create and fill out a .json file for the class in the assets/ folder (example .json file: https://github.com/North101/ghplayertracker/blob/master/app/src/main/assets/class_01.json). e.g. "class_01.json"
* * The key for each entry in "cards" represents an image of that card in the drawable/ folder
(Note: the filename of the .json file without the .json is the class id)
* Add the class id to assets/classes.json
* Rename the appropriate class icon in the drawable/ folder to the class id e.g. "class_01.png". This will be the icon shown on the Classes screen
* Rename the appropirate class icon in the drawable-xxxhdpi/ folder to "icon_" + class id. e.g. "icon_class_01.png". This will be the icon for the toolbar.
