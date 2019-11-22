包和类的简介
主要的包是jsqlparseutils，下面有一个下一级别的包operate，该包下面包含了几个基本操作的类。
主要的接口就是ParseSQL,该接口有一个parse方法，两个参数，分别是需要解析的参数和替换为何种占位符，operate包下的类都实现了该接口。
SQLUtil是一个工具类
SQLParserProcessor这个类是一个我们使用的类，他有个一个parse方法，当传入不同类型的参数的时候调用不同的接口实现类。
也可以直接调用那四个基本操作类。

String sql = "select * from tb_table where id = 3 and age = 4;"
String res = new SelectParser().parse(sql,"?");

或者
String sql = "select * from tb_table where id = 3 and age = 4;"
String res = new SQLParserProcessor(new SelectParser(sql,"?"));