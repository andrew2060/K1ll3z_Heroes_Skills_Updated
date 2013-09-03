package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterDamageManager;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Setting;
import java.util.Iterator;
import java.util.List;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillThunderClap extends ActiveSkill
{
  private String applyText;
  private String expireText;

  public SkillThunderClap(Heroes plugin)
  {
    super(plugin, "ThunderClap");
    setDescription("You send a devastating shockwave that deals $1 damage and slows enemies around you!");
    setUsage("/skill thunderclap");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill thunderclap" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.SILENCABLE, SkillType.DEBUFF, SkillType.MOVEMENT });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(Setting.RADIUS.node(), Integer.valueOf(10));
    node.set(Setting.DAMAGE.node(), Integer.valueOf(10));
    node.set("speed-multiplier", Integer.valueOf(2));
    node.set(Setting.DURATION.node(), Integer.valueOf(15000));
    node.set(Setting.APPLY_TEXT.node(), "%target% has been slowed by %hero%!");
    node.set(Setting.EXPIRE_TEXT.node(), "%target% is no longer slowed!");
    return node;
  }

  public void init()
  {
    this.applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT, "%target% has been slowed by %hero%!").replace("%target%", "$1").replace("%hero%", "$2");
    this.expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%target% is no longer slowed!").replace("%target%", "$1");
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 15000, false);
    int multiplier = SkillConfigManager.getUseSetting(hero, this, "speed-multiplier", 2, false);
    if (multiplier > 20) {
      multiplier = 20;
    }
    SlowEffect cEffect = new SlowEffect(this, duration, multiplier, true, this.applyText, this.expireText, hero);
    int radius = SkillConfigManager.getUseSetting(hero, this, Setting.RADIUS, 10, false);
    List entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
    Iterator i$ = entities.iterator();

    while (i$.hasNext())
    {
      Entity n = (Entity)i$.next();
      if ((n instanceof LivingEntity)) {
        LivingEntity target = (LivingEntity)n;
        if (!target.equals(player))
        {
          int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
          target.getWorld().strikeLightningEffect(target.getLocation());

          this.plugin.getDamageManager().addSpellTarget(target, hero, this);
          damageEntity(target, player, damage, EntityDamageEvent.DamageCause.LIGHTNING);
        }
        Player p = (Player)n;
        Hero tHero = this.plugin.getCharacterManager().getHero(p);
        tHero.addEffect(cEffect);
      }
    }
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
    return getDescription();
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillThunderClap
 * JD-Core Version:    0.6.2
 */