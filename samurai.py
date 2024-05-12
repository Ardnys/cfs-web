# thanks to https://github.com/xtekky/gpt4free
# pip install -U g4f[all]
from g4f.client import AsyncClient
import asyncio
from aiohttp import web

# ignore the UserWarning
asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())

client = AsyncClient()

async def summarize(request):
    summary_content = await request.text()

    prompt = "summarize the following content and return only the summary:"
    task = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt + summary_content}]
    )

    response = await asyncio.gather(task)
    summary = response[0].choices[0].message.content

    return web.Response(text=summary)

app = web.Application()
app.add_routes([web.post('/summarize', summarize)])

if __name__ == "__main__":
    web.run_app(app, host='localhost', port=7878)
