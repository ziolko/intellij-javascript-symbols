# What is this for?

In JavaScript we quite often use strings as some kind of identifiers. Event and module names are good examples here.
 
A good IDE should support this in my opinion. With this plugin on board each JavaScript string
 in format ':hyphen-identifier' becomes a 'symbol' which you can rename(Shift + F6) or find usages of (Alt + F7).
 
After installing, this plugin will recognize 'symbols' and will highlight them blue:
[[https://github.com/ziolko/intellij-javascript-symbols/blob/master/images/first-usage.png]]

Symbols which appear only once in whole project are marked with warning:
[[https://github.com/ziolko/intellij-javascript-symbols/blob/master/images/not-referenced.png]]

Referenced symbols are shown without warning:
[[https://github.com/ziolko/intellij-javascript-symbols/blob/master/images/referenced-symbol.png]]

Symbol names will appear in the suggestions list (Ctrl + Space Bar):
[[https://github.com/ziolko/intellij-javascript-symbols/blob/master/images/completion.png]]

You can easily find usages of symbol with Alt + F7:
[[https://github.com/ziolko/intellij-javascript-symbols/blob/master/images/find-usage.png]]

Last but not least you can rename your symbol with Shift + F6:
[[https://github.com/ziolko/intellij-javascript-symbols/blob/master/images/refactor.png]]

# Contribution
This plugin is definitely not finished. If you've found it useful feel free to contribute. 

# License
https://opensource.org/licenses/MIT