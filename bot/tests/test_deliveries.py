import pytest
from handlers.deliveries import set_delivery, select_all, clear_all


@pytest.fixture
def deliveries():
    return {
        "a": {"id": 1, "name": "A", "excluded": 0},
        "b": {"id": 2, "name": "B", "excluded": 0},
    }


async def test_toggle(cb, state, deliveries):
    state.get_data.return_value = {"deliveries": deliveries}
    cb.data = "choice_a"
    await set_delivery(cb, state)
    saved = state.update_data.call_args.kwargs["deliveries"]
    assert saved["a"]["excluded"] == 1
    assert saved["b"]["excluded"] == 0


async def test_select_all(cb, state, deliveries):
    for v in deliveries.values():
        v["excluded"] = 1
    state.get_data.return_value = {"deliveries": deliveries}
    await select_all(cb, state)
    saved = state.update_data.call_args.kwargs["deliveries"]
    assert all(v["excluded"] == 0 for v in saved.values())


async def test_clear_all(cb, state, deliveries):
    state.get_data.return_value = {"deliveries": deliveries}
    await clear_all(cb, state)
    saved = state.update_data.call_args.kwargs["deliveries"]
    assert all(v["excluded"] == 1 for v in saved.values())
