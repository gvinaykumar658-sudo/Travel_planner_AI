from .hf_service import hf_call


class CityAgent:
    def run(self, city):
        return city.capitalize()


class BudgetAgent:
    def run(self, city, budget, people, days):
        prompt = f"""
        Break down ₹{budget} for {people} people for {days} days in {city}.

        Include:
        - Hotel
        - Food
        - Transport
        - Activities

        Give INR values clearly.
        """
        return hf_call(prompt)


class TransportAgent:
    def run(self, city):
        prompt = f"""
        Suggest best travel options to {city} and local transport.
        Keep it short.
        """
        return hf_call(prompt)


class FoodAgent:
    def run(self, city):
        prompt = f"""
        Suggest popular foods and average meal cost in {city}.
        """
        return hf_call(prompt)


class ItineraryAgent:
    def run(self, city, days, people):
        prompt = f"""
        Create EXACTLY {days} days travel itinerary for {city} for {people} people.

        STRICT RULES:
        - MUST include all days from Day 1 to Day {days}
        - Each day only 2-3 short points
        - NO introduction
        - NO explanation
        - Keep total output under 300 words
        - DO NOT stop early

        FORMAT ONLY:

        Day 1:
        - ...
        - ...
        - ...

        Day 2:
        - ...
        - ...
        - ...

        Continue until Day {days}
        """

        return hf_call(prompt)


def run_agents(city, budget, people, days):
    city_data = CityAgent().run(city)
    print(f"CityAgent output: {city_data}")
    budget_data = BudgetAgent().run(city_data, budget, people, days)
    print(f"BudgetAgent output: {budget_data}")
    transport_data = TransportAgent().run(city_data)
    print(f"TransportAgent output: {transport_data}")
    food_data = FoodAgent().run(city_data)
    print(f"FoodAgent output: {food_data}")

    itinerary_data = ItineraryAgent().run(city_data, days, people)
    print(f"ItineraryAgent output: {itinerary_data}")

    return {
        "city": city_data,
        "budget": budget_data,
        "transport": transport_data,
        "food": food_data,
        "itinerary": itinerary_data,
        "people": people,
        "days": days,

        "agents": {
            "city_agent": city_data,
            "budget_agent": budget_data,
            "transport_agent": transport_data,
            "food_agent": food_data,
            "itinerary_agent": itinerary_data
        }
    }