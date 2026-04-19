from aiogram.fsm.state import StatesGroup, State


class FilterStates(StatesGroup):
    waiting_for_input = State()
