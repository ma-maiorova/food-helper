from handlers.start import cmd_start, cmd_filters, cmd_delivery


async def test_start(msg):
    await cmd_start(msg)
    msg.answer.assert_called_once()


async def test_filters(msg):
    await cmd_filters(msg)
    msg.answer.assert_called_once()


async def test_delivery(msg, state):
    state.get_data.return_value = {"deliveries": {"a": {"id": 1, "name": "A", "excluded": 0}}}
    await cmd_delivery(msg, state)
    msg.answer.assert_called_once()
