class WordCounter:
    def __init__(self):
        self

class WordCounterImpl(WordCounter):


    def process(self, file_content_list):
        thisdict = {}
        for word in file_content_list:
            if word in thisdict:
                thisdict[word] = thisdict.get(word) + 1
            else:
                thisdict[word] = 1
        return thisdict