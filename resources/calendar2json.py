import csv
import json

always_first = False
debug = False

print("""--------------------
Hint    The format of the calendar will have to be
Hint
Hint    [Calendars titles,.. or empty]
Hint    'Key', 'Translation', ['1' - '12'], [Empty],.. ['1' - '12']
Hint    [Keyname], [Translation], [Marker], [Empty],.. [Marker]
Hint
Hint    where multiple calendars are optional.
Hint    In case of multiple calendars you can select one option.
Hint
Hint    You can set the program to always take the first one in the code under `always_first`.
Hint
Hint    A marker can consist of
Hint      - X and x for full month and half month (! \/)
Hint        - ! Be careful that if there are full months, for the sake of simplification ALL small x will be eliminated
Hint      - Anything else for full month
--------------------""")

filename = input("Path to csv calendar file > ")
final_json = {
    "food": []
}
merges = []

def process_ingredient(ingredient, calendar_count):
    # Get only calendar with values
    calendars = list(filter(lambda calendar: not all(
        string == '' or string.isspace() or string == '?' for string in calendar), [
        ingredient[2 + i * (12 + 1): 2 + i + (i + 1) * 12] for i in range(calendar_count)
    ]))
    for i in range(len(calendars)):  # Process small x
        # Case 1: x x x X -> - - - X
        # Case 2: X x x x -> X - - -
        # Case 3:   x x   -> - X
        # We can be sure there is a real season and can erase the small x
        if 'X' not in calendars[i]:
            original_cal = calendars[i][:]
            for x in range(13):  # Go from 12 over 1 to 12
                x -= 1

                if x < 11 and calendars[i][x+1] == 'x' and original_cal[x] == 'x':
                    calendars[i][x+1] = 'X'
                # Eliminate last small x in a row while not eliminating if it's the only one
                elif (x > 0 and calendars[i][x-1] == 'X' or x == 0 and calendars[i][11] == 'X') and \
                    original_cal[x] == 'x' and \
                        (x < 11 and calendars[i][x+1] == '' or x == 11 and calendars[i][0] == ''):
                    calendars[i][x] = ''

        calendars[i] = list(
            map(lambda val: '' if val == 'x' else val, calendars[i]))

    if debug:
        print(ingredient[0], calendars)
        input()

    if len(calendars) == 0:
        print(f"Skipping {ingredient[0]}; empty")
        return

    name_parts = ingredient[0].split('_')
    for part in name_parts:  # Search for similar names and ask if elimination is ok
        for name in map(lambda val: val['name'], final_json['food']):
            if part not in ['common'] and part in name:
                if input(f'\nIs {ingredient[0]} about the same regarding seasons as {name} AND the both about the same ingredient?'
                                 f"\n{name}: {[ month + 1 for month in list(filter(lambda food: food['name'] == name, final_json['food']))[0]['months']]}"
                                 f'\n{ingredient[0]}: {", ".join(str(calendar) for calendar in calendars)} [y | >n< | 1 | 2] > ') in ["y", "Y", '1']:

                    if new_name := input(f"Enter a new name for {name} or leave it blank to do no change > "):
                        final_json['food'][next((index for (index, food) in enumerate(final_json['food']) if food["name"] == name), None)]['name'] = new_name  # https://stackoverflow.com/a/4391722
                        
                    if new_months := input("Enter new months in the format of '6, 7, 10' or leave blank > "):
                        final_json['food'][next((index for (index, food) in enumerate(final_json['food']) if food["name"] == name), None)]['months'] = [month - 1 for month in new_months.split(', ')]

                    merges.append({
                        'original': name,
                        'eliminated': ingredient[0],
                        'new_name': new_name
                    })
                    print(f"Using {name if new_name == '' else new_name + ' (originally ' + name +')'} for {ingredient[0]} thus deleting {ingredient[0]}")
                    return
                
    months = []

    for i in range(12):  # i = month
        values = list(filter(lambda val: not (val == '' or val.isspace() or val == '?'),
                                [calendar[i] for calendar in calendars]))

        if len(values) == 0:
            continue
        elif len(values) > 1 and not always_first:
            take = input(
                f'We got {values} for {ingredient[0]} in the {i+1}. month. Which one do you want to take [1 - {len(calendars)}] > ')
            take = 0 if take == '' else int(take) - 1
        else:
            take = 0

        if values[take] == '' or values[take].isspace() or values[take] == '?':
            continue
        else:
            months.append(i)

        if debug:
            print(months)

    final_json['food'].append({
        "name": ingredient[0],
        "months": months
    })

    print(f"Added {ingredient[0]} for {months}")
    if debug:
        input()

with open(filename, 'r') as file:
    reader = csv.reader(file)
    next(reader)

    calendar_count = next(reader).count('12')
    print(str(calendar_count) + ' calendar detected')

    for row in reader:  # Row = Ingredient
        process_ingredient(row, calendar_count)

final_json = json.dumps(final_json, separators=(', ', ': '))

out = input(
    "\nGive the path to a JSON file or leave blank for output directly to the console > ")
if out == '':
    print(str(final_json))
else:
    if not out.endswith('.json'):
        out += 'calendar.json'
    with open(out, 'w') as output:
        output.write(final_json)

print("Merges: ---------- You might use this in a pull request ------------\n" + 
            '\n'.join(f"{merge['eliminated']} --> {merge['original']}{' --> ' + merge['new_name'] if merge['new_name'] else ''}" for merge in merges) +
            '\n----------------------------------------------------------------------------------\n')

input("Finished. Press enter to exit")
