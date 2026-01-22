# Neo4j Entity

## 职责
定义 Neo4j 图数据库节点和关系的实体类。

## 主要实体

### KGNode.java
知识图谱节点基类
- 节点 ID、标签、属性

### MaterialNode.java
资料节点
- 资料类型、内容
- 向量表示

### StudentNode.java
学生节点
- 学生信息
- 学习轨迹

### KnowledgePointNode.java
知识点节点
- 知识点信息
- 难度等级

### KGRelationship.java
知识图谱关系
- 关系类型
- 源节点、目标节点
- 权重

## 关系类型
- BELONGS_TO - 属于
- REFERENCES - 参考
- MASTERS - 掌握
- WEAK_IN - 薄弱
- RELATED_TO - 相关

