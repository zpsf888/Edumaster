
from langchain.vectorstores import FAISS
from langchain.embeddings import HuggingFaceEmbeddings
from langchain.document_loaders import TextLoader
from langchain.text_splitter import CharacterTextSplitter
from langchain.docstore.document import Document
import os

# 使用中文 embedding 模型
embedding_model = HuggingFaceEmbeddings(
    model_name="shibing624/text2vec-base-chinese"
)

# 文本分割器
text_splitter = CharacterTextSplitter(chunk_size=300, chunk_overlap=50)

def load_documents(folder_path="./docs"):
    docs = []
    for filename in os.listdir(folder_path):
        if filename.endswith(".txt"):
            loader = TextLoader(os.path.join(folder_path, filename), encoding="utf-8")
            doc = loader.load()
            docs.extend(text_splitter.split_documents(doc))
    return docs

# 构建向量库
def build_vector_store():
    docs = load_documents()
    db = FAISS.from_documents(docs, embedding_model)
    db.save_local("faiss_db")
    print("✅ 向量库已构建完成并保存！")

# 加载向量库 & 检索 top_k
def get_relevant_docs(query, k=3):
    db = FAISS.load_local("chatglm3_6b_langchain_rag/faiss_db", embedding_model, allow_dangerous_deserialization=True)
    results = db.similarity_search(query, k=k)
    return "\n".join([doc.page_content for doc in results])
