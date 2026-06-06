from handlers.start import cmd_start, cmd_filters, cmd_delivery, cmd_help


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


async def test_help_clears_state(msg, state):
    await cmd_help(msg, state)
    state.clear.assert_called_once()
    msg.answer.assert_called_once()
