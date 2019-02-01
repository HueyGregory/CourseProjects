import re

class NonABCFilterer:
    def __init__(self):
        pass

class NonABCFiltererImpl(NonABCFilterer):

    def process(self, file_content_list):
        updated_list = []
        for word in file_content_list:
            updated_list.append(re.sub('[^0-9a-z]+', '', word))
        return updated_list
