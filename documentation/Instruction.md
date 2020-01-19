# Instruction

## Get Start

```
请参考：https://github.com/ont-bizsuite/signing-addon/blob/master/documentation/Instruction.md
```

claim-addon配置说明：
```json
{
  "issuer": [
    {
      "id": "demo1",
      "context": "demo1 claim",
      "properties": {
        "age": {
          "type": "INT",
          "description": "age"
        },
        "name": {
          "type": "STRING",
          "description": "name"
        }
      },
      "issuerNamespace": "xxx.app.ont",
      "view": "http://view.com"
    },
    {
      "id": "demo2",
      "context": "demo2 claim",
      "properties": {
        "age": {
          "type": "INT",
          "description": "age"
        },
        "name": {
          "type": "STRING",
          "description": "name"
        }
      },
      "issuerNamespace": "xxx.app.ont",
      "view": "http://view.com"
    }
  ],
  "consumer": [
    {
      "issueBy": "",
      "condition": [
        {
          "key": "",
          "optional": false,
          "type": "STRING",
          "op": "EQ",
          "val": ""
        },
        {
          "key": "",
          "optional": false,
          "type": "INT",
          "op": "GT",
          "val": ""
        }
      ]
    }
  ]
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| issuer     | List | 颁发claim的模板                     |
| id    | String | claim模板id                        |
| context       | String    | claim标题                        |
| properties        | Map | claim内容 |
| age,name     | String | 自定义claim的Key，可任意数量     |
| type        | String | claim的Key的字段类型 |
| description        | String | claim的Key的字段类型 |
| issuerNamespace        | String | 颁发者域名 |
| view        | String | 颁发者视图 |
| consumer        | List | 需求者claim模板 |
| issueBy        | String | 所需claim的颁发者ONT ID |
| condition        | List | 所需claim的条件 |
| key        | String | 所需claim的Key |
| optional        | String | Key是否必要 |
| type        | String | Key的字段类型 |
| op        | String | Key的字段条件：LT-小于,LE-小于等于,EQ-等于,GE-大于等于,GT-大于 |
| val        | String | Key的字段对比值 |


6.使用SDK开发应用：
```
将claim_config.json文件放在项目根目录的config目录下，以便java-sdk在初始化时，读取该配置
sdk方法说明：https://github.com/ont-bizsuite/claim-sdk-java
```