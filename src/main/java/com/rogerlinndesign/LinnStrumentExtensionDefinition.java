package com.rogerlinndesign;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;

public class LinnStrumentExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("d535bbf0-9359-43fd-a3b1-c9831777f894");

   public LinnStrumentExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "LinnStrument";
   }

   @Override
   public String getAuthor()
   {
      return "Claes";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }

   @Override
   public String getHardwareVendor()
   {
      return "Roger Linn Design";
   }

   @Override
   public String getHardwareModel()
   {
      return "LinnStrument";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 7;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 1;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 1;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
      list.add(new String[]{"LinnStrument MIDI"}, new String[]{"LinnStrument MIDI"});
   }

   @Override
   public LinnStrumentExtension createInstance(final ControllerHost host)
   {
      return new LinnStrumentExtension(this, host);
   }
}
