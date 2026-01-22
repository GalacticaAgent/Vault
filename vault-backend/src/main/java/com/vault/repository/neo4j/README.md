# Neo4j Repository 层

## 主要仓库接口

### KGNodeRepository.java
知识图谱节点仓库
- 节点创建与更新
- 节点查询
- 节点删除

### KGRelationshipRepository.java
知识图谱关系仓库
- 关系创建
- 关系查询
- 关系删除

## 节点类型
- MaterialNode - 资料节点
- StudentNode - 学生节点
- KnowledgePointNode - 知识点节点

## 关系类型
- BELONGS_TO - 属于
- REFERENCES - 参考
- MASTERS - 掌握
- WEAK_IN - 薄弱
- RELATED_TO - 相关

