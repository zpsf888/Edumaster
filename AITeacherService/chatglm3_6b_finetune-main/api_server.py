from fastapi import FastAPI
from pydantic import BaseModel
from transformers import AutoTokenizer, AutoModel
import torch
from chatglm3_6b_langchain_rag.rag_engine import get_relevant_docs
# 模型路径
model_dir = "./chatglm3-6b-01"

# 加载 tokenizer 和模型（使用 AutoModel）
tokenizer = AutoTokenizer.from_pretrained(model_dir, trust_remote_code=True)
model = AutoModel.from_pretrained(model_dir,trust_remote_code=True, device='cuda')
model = model.eval()

# FastAPI 初始化
app = FastAPI()

# 请求数据结构
class ChatRequest(BaseModel):
    query: str
    history: list[list[str]] = []
    rag: bool = True

# 聊天接口
@app.post("/chat")
async def chat(req: ChatRequest):
    query = req.query
    history = req.history

    if req.rag:
        retrieved_text = get_relevant_docs(query)
        query = f"已知信息如下：\n{retrieved_text}\n请根据上述信息回答：{query}"

    with torch.no_grad():
        response, history = model.chat(tokenizer, query, history=history)

    return {"response": response, "history": history}

