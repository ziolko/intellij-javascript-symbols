# What is this for?

In JavaScript we quite often use strings as some kind of identifiers. Event and module names are good examples here.
 
A good IDE should support this in my opinion. With this plugin on board each JavaScript string
 in format ':hyphen-identifier' becomes a 'symbol' which you can rename(Shift + F6) or find usages of (Alt + F7).
 
# How to install it
You can find this plugin on [IntelliJ plugin repository](https://plugins.jetbrains.com/plugin/8168). 
Take a look at [IntelliJ IDEA documentation](https://www.jetbrains.com/idea/help/installing-updating-and-uninstalling-repository-plugins.html) 
for instructions about installing plugins. 
 
# Features
After installing, this plugin will recognize 'symbols' and will highlight them blue:  
![First usage](https://raw.githubusercontent.com/ziolko/intellij-javascript-symbols/master/images/first-usage.png)

Symbols which appear only once in whole project are marked with warning:  
![Non referenced symbol](https://raw.githubusercontent.com/ziolko/intellij-javascript-symbols/master/images/not-referenced.png)

Referenced symbols are shown without warning:  
![Referenced symbol](https://raw.githubusercontent.com/ziolko/intellij-javascript-symbols/master/images/referenced-symbol.png)

Symbol names will appear in the suggestions list (Ctrl + Space Bar):  
![Symbol completion](https://raw.githubusercontent.com/ziolko/intellij-javascript-symbols/master/images/completion.png)

You can easily find usages of symbol with Alt + F7:  
![Find usages](https://raw.githubusercontent.com/ziolko/intellij-javascript-symbols/master/images/find-usage.png)

Last but not least you can rename your symbol with Shift + F6:  
![Rename symbol](https://raw.githubusercontent.com/ziolko/intellij-javascript-symbols/master/images/refactor.png)

# Contribution
This plugin is definitely not finished. If you've found it useful feel free to contribute. 

# License
https://opensource.org/licenses/MIT