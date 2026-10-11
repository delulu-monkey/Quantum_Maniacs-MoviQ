import uvicorn
import os

if __name__ == "__main__":
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "8000"))
    print(f"==================================================")
    print(f" Starting MoviQ Transit Backend on http://{host}:{port}")
    print(f" Swagger API Docs: http://localhost:{port}/docs")
    print(f" Android Emulator connection address: http://10.0.2.2:{port}")
    print(f"==================================================")
    uvicorn.run("app.main:app", host=host, port=port, reload=True)
