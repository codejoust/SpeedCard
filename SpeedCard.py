#!/usr/bin/env python
import pygtk, gtk, random, gtk.glade, gobject, sys, os

class SpeedCardDB:
    def __init__(self, filename, delim = ','):
        self.delim = delim
        wordfile = open(filename); self.showBack = False
        self.words = list(); self.index = 0
        for word in wordfile:
            if word[0] == '#':
                continue
            self.words.append(word.split(self.delim, 1))
        wordfile.close()
        random.shuffle(self.words)
        self.wlen = len(self.words)
    def rewind(self):
        self.index -= 1
    def next(self):
        if self.index + 1 >= self.wlen:
            self.index = self.showBack = 0
        if self.showBack == 2:
            self.showBack = 0
            return self.words[self.index - 2]
        else:
            self.showBack += 1; self.index += 1
            return self.words[self.index]
    def advanceCard(self):
        card = self.next()
        return {'name': card[0], 'definition': card[1]}

class SpeedCardGUI:
    def __init__(self, scdb, gladefile = 'SpeedCardGUI.glade'):
        self.db = scdb
        self.interface = gtk.glade.XML(gladefile)
        self.window = self.interface.get_widget('window')
        self.window.set_title('Speed Card - Memorization')
        self.addWidgets(['definition','name','speedSpinner', 'status'])
        self.interface.signal_autoconnect({
            "on_closeWindow": self.onCloseWindow,
            "on_updateSpeed": self.onUpdateSpeed,
            "on_pause":       self.pauseCards })
        self.window.show_all()
    def pauseCards(self):
        self.db.rewind()
    def getLabel(self, text):
        return '<span font_desc="sans" size="xx-large">%s</span>' % text
    def addWidgets(self, widgetNames):
        widgetGuide = tuple()
        for name in widgetNames:
            widget = self.interface.get_widget(name)
            setattr(self, name, widget)
        self.setUpdateSpeed(1500)
        self.advance()
    def onUpdateSpeed(self, speedButton):
        self.timeout = int(speedButton.get_value() * 100)
    def setUpdateSpeed(self, value):
        self.speedSpinner.set_value(int(value / 100))
        self.timeout = int(value)
    def onCloseWindow(self, window):
        gtk.main_quit()
        sys.exit(1)
    def advance(self):
        self.refreshCard(self.db.advanceCard())
        gobject.timeout_add(self.timeout, self.advance)
    def showDef(self):
        self.definition.show()
        return False
    def setCard(self, card):
        self.name.set_markup(self.getLabel(card['name']))
        self.definition.set_markup(self.getLabel(card['definition']))
    def refreshCard(self, card):
        self.setCard(card)
        self.definition.hide()
        gobject.timeout_add(int(self.timeout / 3), self.showDef)
        return True

if __name__ == "__main__":
    wordlistloc = sys.argv[1] if len(sys.argv) > 1 else 'wordlist.csv'
    if os.path.exists(wordlistloc):
        db = SpeedCardDB(wordlistloc)
        try:
            gui = SpeedCardGUI(db)
        except RuntimeError:
            print """
            Couldn't find/load glade interface file.
            Is a well-formed SpeedCardGUI.glade in the same directory?
            """
            sys.exit(0)
        gtk.main()
    else:
        print """
        Couldn't find wordlist file. Use a path after SpeedCard.py or 
        create a csv file called wordlist.csv. Format: word,definition
        Comments are delimited by a leading # character.
        """
