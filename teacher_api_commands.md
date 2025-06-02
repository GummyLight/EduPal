# 教师API测试命令集合

## 1. 学生转交问题给老师
```bash
# 基本转交测试
curl -X POST "http://localhost:8080/ai/transTeacher" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=s20250112&questionId=6f13de53-7469-4b1a-99fd-153d28cb649e&teacherId=t20220001"

# 测试空参数（应该返回400错误）
curl -X POST "http://localhost:8080/ai/transTeacher" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=&questionId=&teacherId="
```

## 2. 老师查看转交的问题
```bash
# 查看特定老师的问题
curl -X GET "http://localhost:8080/ai/teacherView?teacherId=t20220001"

# 查看不存在的老师（测试边界情况）
curl -X GET "http://localhost:8080/ai/teacherView?teacherId=nonexistent"
```

## 3. 老师回答问题
```bash
# 数学问题回答
curl -X POST "http://localhost:8080/ai/teacherAnswer" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "teacherId=t20220001&questionId=6f13de53-7469-4b1a-99fd-153d28cb649e&answerContent=叽里咕噜说什么呢，三角函数学会了没有。"

# 物理问题回答
curl -X POST "http://localhost:8080/ai/teacherAnswer" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "teacherId=teacher001&questionId=6f13de53-7469-4b1a-99fd-153d28cb649e&answerContent=叽里咕噜说什么呢，电磁场学会了没有。"

# 测试空答案内容（应该返回错误）
curl -X POST "http://localhost:8080/ai/teacherAnswer" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "teacherId=t20220001&questionId=6f13de53-7469-4b1a-99fd-153d28cb649e&answerContent="
```

## 4. 完整的工作流测试
```bash
# 步骤1: 转交问题
curl -X POST "http://localhost:8080/ai/transTeacher" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=s20250112&questionId=6f13de53-7469-4b1a-99fd-153d28cb649e&teacherId=t20220001"

# 步骤2: 查看问题
curl -X GET "http://localhost:8080/ai/teacherView?teacherId=t20220001"

# 步骤3: 回答问题
curl -X POST "http://localhost:8080/ai/teacherAnswer" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "teacherId=t20220001&questionId=6f13de53-7469-4b1a-99fd-153d28cb649e&answerContent=连同着迷这个炎炎夏日万般滋味那个你"

# 步骤4: 再次查看验证回答
curl -X GET "http://localhost:8080/ai/teacherView?teacherId=t20220001"
```

## 5. JSON格式化输出（可选）
如果你想要格式化JSON输出，可以添加 | jq：
```bash
curl -X GET "http://localhost:8080/ai/teacherView?teacherId=t20220001" | jq
```

## 6. 详细调试信息
如果需要查看详细的请求/响应信息：
```bash
curl -X GET "http://localhost:8080/ai/teacherView?teacherId=t20220001" -v
```

## 测试数据建议：
- 学生ID: s20250112
- 老师ID: t20220001 
- 问题ID: 6f13de53-7469-4b1a-99fd-153d28cb649e
