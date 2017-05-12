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

datafile = "ciao.json"

try:
    with open(datafile) as f:
        total = json.load(f)
except FileNotFoundError:
    total = []

period = sys.argv[1]
card_type = sys.argv[2]

while True:
    new = {}
    new["name"] = input("Insert name: ")
    if new["name"] == '0':
        break
    new["period"] = period
    new["type"] = card_type
    try:
        num_costs = int(input("How many costs?: "))
    except ValueError:
        num_costs = 1
    new["cost"] = []
    for i in range(0, num_costs):
        new["cost"].append(ask_cost())
    new["immediateAction"] = {}
    ia = new["immediateAction"]
    print("Insert immediateAction: ")
    for i in range(0,4):
        tmp = input("res/counc/pickC/mult: ")
        if tmp == "res":
            ia["resources"] = ask_cost()
        elif tmp == "conc":
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
            pa["purpleFinal"] = {}
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
                    pa["production"]["multiplier"]["type"] = int(input("Insert multiplier type: "))
                    print("Insert multiplier Bonus: ")
                    pa["production"]["multiplier"]["bonus"] = ask_cost()
                elif tmp == "resources":
                    print("Insert production resources: ")
                    pa["production"]["resources"] = ask_cost()
                elif tmp == "councilP":
                    pa["production"]["councilPrivilege"] = int(input("Insert councilPrivilege: "))
                elif tmp == "conversion":
                    pa["production"]["conversion"] = []
                    paPP = pa["production"]["conversion"]
                    while True:
                        conversion_instance = {}
                        conversion_instance["src"] = []
                        print("Insert source resources: ")
                        tmp = ask_cost()
                        conversion_instance["src"].append(tmp)
                        conversion_instance["dest"] = []
                        print("Insert destination resources: ")
                        tmp = ask_cost()
                        conversion_instance["dest"].append(tmp)
                        tmp = {int(input("Insert councilPrivilege: "))}
                        conversion_instance["dest"].append(tmp)
                        paPP.append(conversion_instance)
                        another = input("second Alternative?: ")
                        if another == "yes":
                            continue
                        elif another == "no":
                            break
                elif tmp == '0':
                    break
        elif tmp == "towerB":
            pa["towerBonus"]["type"] = input("Insert towerBonus type: ")
            pa["towerBonus"]["plusValue"] = int(input("Insert towerBonus plusValue: "))
            print("Insert towerBonus discount: ")
            num_costs = int(input("How many costs?: "))
            for i in range(0,num_costs):
                pa["towerBonus"]["discount"] = ask_cost()
        elif tmp == "harvestPV":
            pa["harvestPlusValue"] == int(input("insert harvestPlusValue: "))
        elif tmp == "prodPV":
            pa["productionPlusValue"] = int(input("Insert productionPlusValue: "))
        elif tmp == "boycottTB":
            pa["boycottInstantTowerBonus"] = bool(input("Insert boycottInstantTowerBonus: "))
        elif tmp == '0':
            break
    total.append(new)
    with open(datafile, 'w') as f:
        json.dump(total, f, sort_keys=True, indent=4)
