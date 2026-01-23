# Milvus 快速启动脚本

Write-Host "🚀 启动 Milvus 向量数据库..." -ForegroundColor Green
Write-Host ""

# 检查 Docker 是否运行
$dockerRunning = docker info 2>$null
if (-not $?) {
    Write-Host "❌ Docker 未运行！请先启动 Docker Desktop" -ForegroundColor Red
    Write-Host ""
    Write-Host "请执行以下步骤:" -ForegroundColor Yellow
    Write-Host "1. 启动 Docker Desktop" -ForegroundColor Yellow
    Write-Host "2. 等待 Docker 完全启动" -ForegroundColor Yellow
    Write-Host "3. 重新运行此脚本" -ForegroundColor Yellow
    pause
    exit 1
}

Write-Host "✅ Docker 正在运行" -ForegroundColor Green
Write-Host ""

# 启动 Milvus
Write-Host "📦 启动 Milvus 容器..." -ForegroundColor Cyan
docker-compose -f docker-compose-milvus.yml up -d

if ($?) {
    Write-Host ""
    Write-Host "✅ Milvus 启动成功！" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Milvus 信息:" -ForegroundColor Cyan
    Write-Host "  - 端口: 19530" -ForegroundColor White
    Write-Host "  - MinIO 控制台: http://localhost:9001" -ForegroundColor White
    Write-Host "  - MinIO 用户名: minioadmin" -ForegroundColor White
    Write-Host "  - MinIO 密码: minioadmin" -ForegroundColor White
    Write-Host ""
    Write-Host "🔍 查看容器状态:" -ForegroundColor Cyan
    docker-compose -f docker-compose-milvus.yml ps
    Write-Host ""
    Write-Host "📝 查看日志命令: docker-compose -f docker-compose-milvus.yml logs -f" -ForegroundColor Yellow
    Write-Host "🛑 停止服务命令: docker-compose -f docker-compose-milvus.yml down" -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "❌ Milvus 启动失败！" -ForegroundColor Red
    Write-Host "请检查日志: docker-compose -f docker-compose-milvus.yml logs" -ForegroundColor Yellow
}

Write-Host "按任意键继续..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
