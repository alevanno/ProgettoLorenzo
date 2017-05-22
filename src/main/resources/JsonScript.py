#!  /usr/bin/env python3

import sys
import json


def ask_cost():
    new_cost = {}
    for i in ["coin", "stone", "wood", "servant", "victoryPoint", "militaryPoint", "faithPoint"]:
        try:
            new_cost[i] = int(input("Insert {}: ".format(i)))
        except ValueError:
            new_cost[i] = 0
    return dict((x, y) for x, y in new_cost.items() if y != 0)

datafile = "cards.json"

try:
    with open(datafile) as f:
        total = json.load(f)
except FileNotFoundError:
    total = []


while True:
    new = {}
    new["name"] = input("Insert card name: ")
    if new["name"] == '0':
        break
    new["period"] = int(input("Insert card period "))
    new["type"] = "ventures"
    try:
        num_costs = int(input("How many costs?: "))
    except ValueError:
        num_costs = 1
    new["cost"] = []
    for i in range(0, num_costs):
        new["cost"].append(ask_cost())
        if new["type"] == "ventures":
            try:
                new["minMilitaryPoint"] = int(input("Insert minMilitaryPoint: "))
            except ValueError:
                pass
    new["immediateAction"] = {}
    ia = new["immediateAction"]
    print("Insert immediateAction: ")
    for i in range(0,4):
        tmp = input("res/counc/pickC/mult: ")
        if tmp == "res":
            ia["resources"] = ask_cost()
        elif tmp == "counc":
            ia["councilPrivilege"] = int(input("Insert councilPrivilege: "))
        elif tmp == "pickC":
            ia["pickCard"] = {}
            ia["pickCard"]["type"] = input("Insert card type: ")
            ia["pickCard"]["value"] = int(input("Insert card value: "))
            num_costs = int(input("How many costs?: "))
            for i in range(0,num_costs):
                ia["pickCard"]["discount"] = ask_cost()
        elif tmp == "mult":
            ia["multiplier"] = {}
            ia["multiplier"]["type"] = input("Insert type of multiplier: ")
            ia["multiplier"]["bonus"] = ask_cost()
        elif tmp == '0':
            break
    new["permanentAction"] = {}
    pa = new["permanentAction"]
    print("Insert permanentAction: ")
    for i in range(0,8):
        tmp = input("harv/purpleFinal/cardsV/prod/towerB/harvestPV/prodPV/boycottTB: ")
        if tmp == "harv":
            pa["harvest"] = {}
            pa["harvest"]["value"] = int(input("Insert value: "))
            pa["harvest"]["resources"] = ask_cost()
            tmp = input("is there a councilPrivilege?: ")
            if tmp == "yes":
                pa["harvest"]["councilPrivilege"] = int(input("Insert councilPrivilege: "))
        elif tmp == "purpleFinal":
            pa["purpleFinal"] = ask_cost()
        elif tmp == "cardsV":
            pa["cardsValue"] = {}
            pa["cardsValue"]["type"] = input("Insert card type: ")
            pa["cardsValue"]["modifier"] = int(input("Insert card modifier: "))
        elif tmp == "prod":
            pa["production"] = {}
            pa["production"]["value"] = int(input("Insert value: "))
            print("Insert production features: ")
            for i in range(0,4):
                tmp = input("multiplier/resources/councilP/conversion: ")
                if tmp == "multiplier":
                    pa["production"]["multiplier"] = {}
                    pa["production"]["multiplier"]["type"] = input("Insert multiplier card type: ")
                    print("Insert multiplier res Bonus: ")
                    pa["production"]["multiplier"]["bonus"] = ask_cost()
                elif tmp == "resources":
                    print("Insert production resources: ")
                    pa["production"]["resources"] = ask_cost()
                elif tmp == "councilP":
                    pa["production"]["councilPrivilege"] = int(input("Insert councilPrivilege: "))
                elif tmp == '0':
                    break
        elif tmp == "towerB":
            pa["towerBonus"] = {}
            pa["towerBonus"]["type"] = input("Insert towerBonus type: ")
            pa["towerBonus"]["plusValue"] = int(input("Insert towerBonus plusValue: "))
            print("Insert towerBonus discount: ")
            num_costs = int(input("How many costs?: "))
            for i in range(0,num_costs):
                pa["towerBonus"]["discount"] = ask_cost()
        elif tmp == "harvestPV":
            pa["harvestPlusValue"] = {}
            pa["harvestPlusValue"] == int(input("insert harvestPlusValue: "))
        elif tmp == "prodPV":
            pa["productionPlusValue"] = {}
            pa["productionPlusValue"] = int(input("Insert productionPlusValue: "))
        elif tmp == "boycottTB":
            pa["boycottInstantTowerBonus"] = {}
            pa["boycottInstantTowerBonus"] = bool(input("Insert boycottInstantTowerBonus: "))
        elif tmp == '0':
            break
    total.append(new)
    with open(datafile, 'w') as f:
        json.dump(total, f, sort_keys=True, indent=4)
