# 声明一个张量不会在计算图中增加什么，只有在赋值给变量或者占位符之后的才有意义

# 固定维度的
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.externals import joblib
import pandas
count_1=joblib.load("C:\\Users\\guowanghao\\a_graduation\\count_vect.m")
tfidf_1=joblib.load("C:\\Users\\guowanghao\\a_graduation\\tfidf_vect.m")
#读取完成分类的模型
estimator=joblib.load("C:\\Users\\guowanghao\\a_graduation\\NB_count.m") #这里仅仅演示一种操作
#estimator=joblib.load("C:\\Users\\guowanghao\\a_graduation\\XGboost_count.m")
test=pandas.DataFrame()
test['text']=["A FIVE star book"]
test_x=count_1.transform(test['text'])
#test_x=tfidf_1.transform(test['text'])
print(estimator.predict(test_x).shape)
