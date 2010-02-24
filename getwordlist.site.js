Bookmarklet to extract the words from 
http://wordsgalore.com/wordsgalore/languages/spanish/spanish1000.html


(function(){

var tags = document.getElementsByTagName('td'); for (tag in tags){ if (tags[tag].innerHTML){words += tags[tag].innerHTML;} }
var words = words.split('<br>');

var wordsOut = [];
for (word in words){
var word = words[word];
word = word.split(/<\/a>/);
word[0] = word[0].replace(/<a href=.*>/,'').replace(/\n/,'');
wordsOut.push(word); }


document.body.innerHTML = '';
for (daword in wordsOut){
zeword = wordsOut[daword];
document.body.innerHTML += zeword.join(",") + '<br />'; }

})();
